package ch.cern.atlas.apvs.server;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.hibernate.HibernateException;

import ch.cern.atlas.apvs.client.service.EventService;
import ch.cern.atlas.apvs.client.service.ServiceException;
import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.SortOrder;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class EventServiceImpl extends ResponsePollService implements EventService {

	private Database database;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		database = Database.getInstance();
	}
	
	@Override
	public long getRowCount() throws ServiceException {
		try {
			return database.getCount(Event.class);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public long getRowCount(Device device, String sensor) throws ServiceException {
		try {
			return database.getEventCount(device, sensor);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Event> getTableData(int start, int length, SortOrder[] order)
			throws ServiceException {
		try {
			return database.getList(Event.class, start, length, order);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Event> getTableData(int start, int length, SortOrder[] order, Device device, String sensor)
			throws ServiceException {
		try {
			return database.getEvents(start, length, order, device, sensor);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}	
}
