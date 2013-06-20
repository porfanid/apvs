package ch.cern.atlas.apvs.server;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.service.EventService;
import ch.cern.atlas.apvs.client.service.ServiceException;
import ch.cern.atlas.apvs.client.service.SortOrder;
import ch.cern.atlas.apvs.domain.Event;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class EventServiceImpl extends ResponsePollService implements EventService {

	private DbHandler dbHandler;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		dbHandler = DbHandler.getInstance();
	}
	
	@Override
	public int getRowCount() throws ServiceException {
		try {
			return dbHandler.getEventCount(null, null);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public int getRowCount(String ptuId, String measurementName) throws ServiceException {
		try {
			return dbHandler.getEventCount(ptuId, measurementName);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Event> getTableData(int start, int length, SortOrder[] order)
			throws ServiceException {
		try {
			return dbHandler.getEvents(start, length, order, null, null);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Event> getTableData(int start, int length, SortOrder[] order, String ptuId, String measurementName)
			throws ServiceException {
		try {
			return dbHandler.getEvents(start, length, order, ptuId, measurementName);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
