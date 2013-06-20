package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.domain.Event;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsEvent")
public interface EventService extends TableService<Event>, RemoteService {
	int getRowCount(String ptuId, String measurementName) throws ServiceException;
	
	List<Event> getTableData(int start, int length, SortOrder[] order, String ptuId, String measurementName)
			throws ServiceException;
}
