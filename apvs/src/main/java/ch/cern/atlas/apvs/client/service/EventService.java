package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.SortOrder;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsEvent")
public interface EventService extends TableService<Event>, RemoteService {
	long getRowCount(Device device, String sensor) throws ServiceException;
	
	List<Event> getTableData(int start, int length, SortOrder[] order, Device device, String sensor)
			throws ServiceException;
}
