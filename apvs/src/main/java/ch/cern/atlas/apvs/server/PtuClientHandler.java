package ch.cern.atlas.apvs.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.PtuSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.db.Scale;
import ch.cern.atlas.apvs.db.SensorMap;
import ch.cern.atlas.apvs.domain.APVSException;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Error;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.GeneralConfiguration;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Order;
import ch.cern.atlas.apvs.domain.Report;
import ch.cern.atlas.apvs.domain.Ternary;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent.ConnectionType;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.server.PtuJsonReader;
import ch.cern.atlas.apvs.ptu.server.PtuJsonWriter;
import ch.cern.atlas.apvs.ptu.server.PtuReconnectHandler;
import ch.cern.atlas.apvs.ptu.shared.EventChangedEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

// FIXME not really sure...but was working in netty 3.5 without this... so was shared...
@Sharable
public class PtuClientHandler extends PtuReconnectHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	private final RemoteEventBus eventBus;

	private List<Measurement> measurementChanged = new ArrayList<Measurement>();

	private Ternary dosimeterOk = Ternary.Unknown;

	private PtuSettings settings;
	
	private Database database;
	private SensorMap sensorMap;

	public PtuClientHandler(Bootstrap bootstrap, final RemoteEventBus eventBus) {
		super(bootstrap);
		this.eventBus = eventBus;
		
		database = Database.getInstance(eventBus);
		
		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(ConnectionStatusChangedRemoteEvent.class
						.getName())) {
					ConnectionStatusChangedRemoteEvent.fire(eventBus,
							ConnectionType.daq, isConnected());
					ConnectionStatusChangedRemoteEvent.fire(eventBus,
							ConnectionType.dosimeter,
							isConnected() ? dosimeterOk : Ternary.False);
				}
			}
		});

		PtuSettingsChangedRemoteEvent.subscribe(eventBus,
				new PtuSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedRemoteEvent event) {
						settings = event.getPtuSettings();
					}
				});
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx)
			throws Exception {
		ConnectionStatusChangedRemoteEvent.fire(eventBus, ConnectionType.daq,
				true);
		ConnectionStatusChangedRemoteEvent.fire(eventBus,
				ConnectionType.dosimeter, dosimeterOk);
		super.channelActive(ctx);
		
		sensorMap = database.getSensorMap();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ConnectionStatusChangedRemoteEvent.fire(eventBus, ConnectionType.daq,
				false);
		ConnectionStatusChangedRemoteEvent.fire(eventBus,
				ConnectionType.dosimeter, dosimeterOk);
		super.channelInactive(ctx);
	}

	public void sendOrder(Order order) {
		try {
			System.out.println(PtuJsonWriter.objectToJson(order));

			ByteBuf buffer = Unpooled.buffer(8192);
			OutputStream os = new ByteBufOutputStream(buffer);
			PtuJsonWriter writer = new PtuJsonWriter(os);
			writer.write(0x10);
			writer.write(order);
			writer.write(0x13);
			System.out.println("Sending...");

			ByteBufOutputStream cos = (ByteBufOutputStream) os;
			getChannel().write(cos.buffer()).awaitUninterruptibly();
			System.out.println(PtuJsonWriter.objectToJson(order));
			writer.close();
			System.out.println("Done...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private final static boolean DEBUG = false;
	private final static boolean DEBUGPLUS = false;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		// Print out the line received from the server.
		// FIXME #634, not sure if this is correct
		String line = msg.toString();

		if (DEBUGPLUS) {
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				System.err.println(c + " " + Integer.toString(c));
			}
		}

		line = line.replaceAll("\u0000", "");
		line = line.replaceAll("\u0010", "");
		line = line.replaceAll("\u0013", "");
		if (DEBUG) {
			log.info("'" + line + "'");
		}
		if (DEBUGPLUS) {
			log.info("LineLength " + line.length());
		}

		List<Message> list;
		try {
			list = PtuJsonReader.jsonToJava(line).getMessages();
			for (Iterator<Message> i = list.iterator(); i.hasNext();) {
				Message message = i.next();
				try {
					if (message instanceof Measurement) {
						handleMessage((Measurement) message);
					} else if (message instanceof Report) {
						handleMessage((Report) message);
					} else if (message instanceof Event) {
						handleMessage((Event) message);
					} else if (message instanceof Error) {
						handleMessage((Error) message);
					} else if (message instanceof GeneralConfiguration) {
						handleMessage((GeneralConfiguration) message);
					} else {
						log.warn("Error: unknown Message Type: "
								+ message.getType());
					}
				} catch (APVSException e) {
					log.warn("Could not add measurement", e);
				}
			}
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}

	}

	private final static long SECOND = 1000;
	private final static long MINUTE = 60 * SECOND;

	private void handleMessage(Measurement message) throws APVSException {

		// Quick fix for #371
		Date now = new Date();
		if (message.getDate().getTime() < (now.getTime() - 5 * MINUTE)) {
			log.warn("UPDATE IGNORED, too old " + message.getDate() + " " + now
					+ " " + message);
			return;
		}

		Device ptu = message.getDevice();
		String sensor = message.getSensor();
		
		if (!sensorMap.isEnabled(ptu, sensor)) {
//			log.warn("UPDATE IGNORED, disabled measurement " + ptuId + " " + sensor);
			return;			
		}
				
		String unit = message.getUnit();
		Double value = message.getValue();
		Double low = message.getLowLimit();
		Double high = message.getHighLimit();

		// Scale down to microSievert
		value = Scale.getValue(value, unit);
		low = Scale.getLowLimit(low, unit);
		high = Scale.getHighLimit(high, unit);
		unit = Scale.getUnit(unit);

		measurementChanged.add(new Measurement(message.getDevice(), sensor, value, low, high, unit, message.getSamplingRate(),
				message.getDate()));

		sendEvents();
	}

	private void handleMessage(Event message) {
		Device device = message.getDevice();
		String sensor = message.getSensor();

		// log.info("EVENT " + message);

		eventBus.fireEvent(new EventChangedEvent(new Event(device, sensor,
				message.getEventType(), message.getValue(), message
						.getThreshold(), message.getUnit(), message.getDate())));

		if (message.getEventType().equals("DosConnectionStatus_OFF")) {
			dosimeterOk = Ternary.False;
			ConnectionStatusChangedRemoteEvent.fire(eventBus,
					ConnectionType.dosimeter, dosimeterOk);
		} else if (message.getEventType().equals("DosConnectionStatus_ON")) {
			dosimeterOk = Ternary.True;
			ConnectionStatusChangedRemoteEvent.fire(eventBus,
					ConnectionType.dosimeter, dosimeterOk);
		}
	}

	private void handleMessage(GeneralConfiguration message) {
		String ptuId = message.getDevice().getName();
		String dosimeterId = message.getDosimeterId();

		if (settings != null) {
			settings.setDosimeterSerialNumber(ptuId, dosimeterId);

			eventBus.fireEvent(new PtuSettingsChangedRemoteEvent(settings));
		}
	}

	private void handleMessage(Report report) {
		log.warn(report.getType() + " NOT YET IMPLEMENTED, see #23 and #112");
	}

	private void handleMessage(Error error) {
		log.warn(error.getType() + " NOT YET IMPLEMENTED, see #114");
	}

	private synchronized void sendEvents() {

		for (Iterator<Measurement> i = measurementChanged.iterator(); i
				.hasNext();) {
			Measurement m = i.next();
			eventBus.fireEvent(new MeasurementChangedEvent(m));
		}

		measurementChanged.clear();
	}
}
