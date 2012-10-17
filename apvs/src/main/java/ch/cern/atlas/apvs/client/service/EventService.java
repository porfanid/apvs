package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.domain.Event;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.view.client.Range;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsEvent")
public interface EventService extends TableService<Event>, RemoteService {
	int getRowCount(String ptuId) throws ServiceException;
	
	List<Event> getTableData(Range range, SortOrder[] order, String ptuId)
			throws ServiceException;
}
