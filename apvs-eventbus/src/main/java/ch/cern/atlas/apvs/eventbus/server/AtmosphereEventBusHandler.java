package ch.cern.atlas.apvs.eventbus.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.gwt.server.AtmosphereGwtHandler;
import org.atmosphere.gwt.server.GwtAtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.eventbus.shared.ConnectionUUIDsChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

/**
 * @author Mark Donszelmann
 */
public class AtmosphereEventBusHandler extends AtmosphereGwtHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private final static boolean DEBUG = true;

	private RemoteEventBus eventBus;

	private ArrayList<String> uuids;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		log.info("AtmosphereEventBusHandler started...");
		java.util.logging.Logger.getLogger("").setLevel(Level.INFO);
		java.util.logging.Logger.getLogger("org.atmosphere.gwt").setLevel(Level.ALL);
		java.util.logging.Logger.getLogger("ch.cern.atlas.apvs").setLevel(Level.ALL);
		java.util.logging.Logger.getLogger("").getHandlers()[0]
				.setLevel(Level.ALL);
		log.trace("Updated logging levels");

		eventBus = ServerEventBus.getInstance();
		uuids = new ArrayList<String>();

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(ConnectionUUIDsChangedEvent.class.getName())) {
					ConnectionUUIDsChangedEvent.fire(eventBus, uuids, null,
							null);
				}
			}
		});
	}

	@Override
	public int doComet(GwtAtmosphereResource resource) throws ServletException,
			IOException {
		if (DEBUG) {
//			log.info("AtmosphereEventBusHandler.doComet()..."
//					+ resource.getConnectionUUID());
//			log.info(resource.getAtmosphereResource()
//					.getAtmosphereResourceEvent().toString());
		}
//		String uuid = resource.getConnectionUUID();
//		if (uuid != null) {
//			uuids.add(uuid);
//			ConnectionUUIDsChangedEvent.fire(eventBus, uuids, uuid, null);
//		}

		resource.getBroadcaster().setID("GWT_COMET");
		HttpSession session = resource.getAtmosphereResource().getRequest()
				.getSession(false);
		if (session != null) {
			log.debug("Got session with id: " + session.getId());
			log.debug("Time attribute: " + session.getAttribute("time"));
		} else {
			log.warn("No session");
		}
		if (log.isDebugEnabled()) {
			log.debug("Url: "
					+ resource.getAtmosphereResource().getRequest()
							.getRequestURL()
					+ "?"
					+ resource.getAtmosphereResource().getRequest()
							.getQueryString());
		}

		String agent = resource.getRequest().getHeader("user-agent");
        logger.info(agent);
//		return DO_COMET_RESUME;
        return NO_TIMEOUT;
	}

	@Override
	public void doPost(HttpServletRequest postRequest,
			HttpServletResponse postResponse, List<?> messages,
			GwtAtmosphereResource resource) {
		super.doPost(postRequest, postResponse, messages, resource);
		if (DEBUG) {
			log.info("AtmosphereEventBusHandler.Post...");
		}
		for (Iterator<?> i = messages.iterator(); i.hasNext();) {
			log.info("-- " + i.next().getClass());
		}
	}

	@Override
	public void broadcast(List<?> messages, GwtAtmosphereResource resource) {
		super.broadcast(messages, resource);
		if (DEBUG) {
			log.info("AtmosphereEventBusHandler.bCast...");
		}
		for (Iterator<?> i = messages.iterator(); i.hasNext();) {
			log.info("-- " + i.next().getClass());
		}
	}

	@Override
	public void broadcast(Object message, GwtAtmosphereResource resource) {
		super.broadcast(message, resource);
		if (DEBUG) {
			log.info("AtmosphereEventBusHandler.bCast..." + message.getClass());
		}
	}

	@Override
	public void cometTerminated(GwtAtmosphereResource cometResponse,
			boolean serverInitiated) {
		super.cometTerminated(cometResponse, serverInitiated);
		if (DEBUG) {
//			log.info("AtmosphereEventBusHandler.Comet terminated "
//					+ cometResponse.getConnectionUUID());
		}
	}

	@Override
	public void disconnect(GwtAtmosphereResource resource) {
		super.disconnect(resource);
		if (DEBUG) {
//			log.info("AtmosphereEventBusHandler.Comet disconnected "
//					+ resource.getConnectionUUID());
//			log.info(resource.getAtmosphereResource()
//					.getAtmosphereResourceEvent().toString());
		}
		
//		String uuid = resource.getConnectionUUID();
//		if (uuid != null) {
//			uuids.remove(uuid);
//			ConnectionUUIDsChangedEvent.fire(eventBus, uuids, null, uuid);
//		}
	}

	@Override
	public void onStateChange(AtmosphereResourceEvent event) throws IOException {
		super.onStateChange(event);
		if (DEBUG) {
			log.info("AtmosphereEventBusHandler.Commet stateChanged "
					+ event.getMessage());
			log.info(event.getResource().uuid());
		}
	}
}
