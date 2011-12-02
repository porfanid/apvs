package ch.cern.atlas.apvs.eventbus.client;

import java.util.List;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("eventBusService")
public interface EventBusService extends RemoteService {
	
	public void fireEvent(RemoteEvent<?> event);
	
	public List<RemoteEvent<?>> getQueuedEvents();
	
}
