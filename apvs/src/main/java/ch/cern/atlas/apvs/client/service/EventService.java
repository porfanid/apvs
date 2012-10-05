package ch.cern.atlas.apvs.client.service;

import ch.cern.atlas.apvs.domain.Event;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsEvent")
public interface EventService extends TableService<Event>, RemoteService {
}
