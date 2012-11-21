package ch.cern.atlas.apvs.eventbus.server;

import java.io.IOException;
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

/**
 * @author Mark Donszelmann
 */
public class AtmosphereEventBusHandler extends AtmosphereGwtHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        log.info("AtmosphereEventBusHandler started...");
        java.util.logging.Logger.getLogger("").setLevel(Level.INFO);
        java.util.logging.Logger.getLogger("gwtcomettest").setLevel(Level.ALL);
        java.util.logging.Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
        log.trace("Updated logging levels");
    }  

    @Override
    public int doComet(GwtAtmosphereResource resource) throws ServletException, IOException {
    	log.info("AtmosphereEventBusHandler.doComet()..."+resource.getConnectionUUID());
    	log.info(resource.getAtmosphereResource().getAtmosphereResourceEvent().toString());
        resource.getBroadcaster().setID("GWT_COMET");
        HttpSession session = resource.getAtmosphereResource().getRequest().getSession(false);
        if (session != null) {
            logger.debug("Got session with id: " + session.getId());
            logger.debug("Time attribute: " + session.getAttribute("time"));
        } else {
            logger.warn("No session");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Url: " + resource.getAtmosphereResource().getRequest().getRequestURL()
                    + "?" + resource.getAtmosphereResource().getRequest().getQueryString());
        }
        return DO_COMET_RESUME;
    }

    @Override    
    public void doPost(HttpServletRequest postRequest, HttpServletResponse postResponse, List<?> messages, GwtAtmosphereResource resource) {
    	super.doPost(postRequest, postResponse, messages, resource);
    	log.info("AtmosphereEventBusHandler.Post...");
    	for (Iterator<?> i = messages.iterator(); i.hasNext(); ) {
    		log.info("-- "+i.next().getClass());
    	}
    }

    @Override
    public void broadcast(List<?> messages, GwtAtmosphereResource resource) {
    	super.broadcast(messages, resource);
    	log.info("AtmosphereEventBusHandler.bCast...");
    	for (Iterator<?> i = messages.iterator(); i.hasNext(); ) {
    		log.info("-- "+i.next().getClass());
    	}
    }
    
    @Override
    public void broadcast(Object message, GwtAtmosphereResource resource) {
    	super.broadcast(message, resource);
    	log.info("AtmosphereEventBusHandler.bCast..."+message.getClass());
    }
       
    @Override
    public void cometTerminated(GwtAtmosphereResource cometResponse, boolean serverInitiated) {
        super.cometTerminated(cometResponse, serverInitiated);
        log.info("AtmosphereEventBusHandler.Comet disconnected "+cometResponse.getConnectionUUID());
    }
    
    @Override
    public void disconnect(GwtAtmosphereResource resource) {
    	super.disconnect(resource);
        log.info("AtmosphereEventBusHandler.Comet disconnected "+resource.getConnectionUUID());
    	log.info(resource.getAtmosphereResource().getAtmosphereResourceEvent().toString());
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
    	// TODO Auto-generated method stub
    	super.onStateChange(event);
    	log.info("AtmosphereEventBusHandler.Commet stateChanged "+event.getMessage());
    	log.info(event.getResource().uuid());

    }
    
    // FIXME #284, its here where we need to broadcast the COnnectionUUIDsChanged event... however disconnect seems not to show up yet... 
    
}
