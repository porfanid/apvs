package ch.cern.atlas.apvs.server;

import java.sql.SQLException;
import java.util.List;

import ch.cern.atlas.apvs.client.service.EventService;
import ch.cern.atlas.apvs.client.service.ServiceException;
import ch.cern.atlas.apvs.client.service.SortOrder;
import ch.cern.atlas.apvs.domain.Event;

import com.google.gwt.view.client.Range;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class EventServiceImpl extends DbServiceImpl implements EventService {

	@Override
	public int getRowCount() throws ServiceException {
		try {
			return dbHandler.getEventCount(null);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public int getRowCount(String ptuId) throws ServiceException {
		try {
			return dbHandler.getEventCount(ptuId);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Event> getTableData(Range range, SortOrder[] order)
			throws ServiceException {
		try {
			return dbHandler.getEvents(range, order, null);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Event> getTableData(Range range, SortOrder[] order, String ptuId)
			throws ServiceException {
		try {
			return dbHandler.getEvents(range, order, ptuId);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
