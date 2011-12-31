package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterReader;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterWriter;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DosimeterServiceImpl extends ResponsePollService implements
		Runnable {

	private static final int RECONNECT_INTERVAL = 20000;
	private static final int DEFAULT_PORT = 4001;
	private static final String name = "Dosimeter Server";
	
	private String host = null;
	private int port = DEFAULT_PORT;
	private boolean stopped = false;
	private Socket socket;
	private String dosimeterUrl;
	private DosimeterReader dosimeterReader;
	private RemoteEventBus eventBus;

	public DosimeterServiceImpl() {
		System.out.println("Creating DosimeterService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("Starting DosimeterService...");
		
		ServerSettingsChangedEvent.subscribe(eventBus, new ServerSettingsChangedEvent.Handler() {
			
			@Override
			public void onServerSettingsChanged(ServerSettingsChangedEvent event) {
				ServerSettings settings = event.getServerSettings();
				if (settings != null) {
					String url = settings.get(ServerSettings.settingNames[1]);
					if ((url != null) && !url.equals(dosimeterUrl)) {
						dosimeterUrl = url;
						String[] s = dosimeterUrl.split(":", 2);
						host = s[0];
						port = s.length > 1 ? Integer.parseInt(s[1]) : DEFAULT_PORT;
						
						if (socket != null) {
							try {
								socket.close();
							} catch (IOException e) {
								// ignored
							}
						}
					}
				}
			}
		});

		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		
		boolean showError = true;

		while (!stopped) {
			if ((dosimeterReader == null) && (host != null)) {
				try {
					if (showError) {
						System.out.println("Trying to connect to "+ name + " on " + host
							+ ":" + port);
					}
				    socket = new Socket(host, port);
					showError = true;
					System.out.println("Connected to " + name + " on " + host
							+ ":" + port);

					DosimeterWriter dosimeterWriter = new DosimeterWriter(
							socket);
				    Thread writerThread = new Thread(dosimeterWriter);
				    writerThread.start();

					dosimeterReader = new DosimeterReader(eventBus, socket);

					Thread readerThread = new Thread(dosimeterReader);
					readerThread.start();
					readerThread.join();
					socket = null;
				} catch (UnknownHostException e) {
					if (showError) {
						System.err.println(getClass() + " " + e);
						showError = false;
					}
				} catch (ConnectException e) {
					if (showError) {
						System.err.println("Could not connect to " + name
								+ " on " + host + ":" + port
								+ ", retrying in a while...");
						showError = false;
					}
				} catch (IOException e) {
					if (showError) {
						System.err.println(getClass() + " " + e);
						showError = false;
					}
				} catch (InterruptedException e) {
					System.err.println(getClass() + " " + e);
				}

				if (dosimeterReader != null) {
					dosimeterReader.close();
				}
				dosimeterReader = null;
			}

			// System.err.println("Sleep");
			try {
				Thread.sleep(RECONNECT_INTERVAL);
			} catch (InterruptedException e) {
				// ignored
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();

		stopped = true;

		if (dosimeterReader != null) {
			dosimeterReader.close();
		}
	}

}
