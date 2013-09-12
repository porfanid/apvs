package ch.cern.atlas.apvs.server;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.hibernate.HibernateException;

import ch.cern.atlas.apvs.client.service.InterventionService;
import ch.cern.atlas.apvs.client.service.ServiceException;
import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Intervention;
import ch.cern.atlas.apvs.domain.SortOrder;
import ch.cern.atlas.apvs.domain.User;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class InterventionServiceImpl extends ResponsePollService implements
		InterventionService {

	private Database database;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		database = Database.getInstance(APVSServerFactory.getInstance().getEventBus());
	}
	
	@Override
	public long getRowCount() throws ServiceException {
		try {
			return database.getCount(Intervention.class);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Intervention> getTableData(int start, int length, SortOrder[] order)
			throws ServiceException {
		try {
			return database.getList(Intervention.class, start, length, order);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void addUser(User user) throws ServiceException {
		try {
			database.saveOrUpdate(user, false);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void addDevice(Device device)
			throws ServiceException {
		try {
			database.saveOrUpdate(device, false);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Device> getDevices(boolean notBusy) throws ServiceException {
		try {
			return database.getDevices(notBusy);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void addIntervention(Intervention intervention) throws ServiceException {
		try {
			database.saveOrUpdate(intervention, true);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void updateIntervention(Intervention intervention) throws ServiceException {
		try {
			database.saveOrUpdate(intervention, true);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<User> getUsers(boolean notBusy) throws ServiceException {
		try {
			return database.getUsers(notBusy);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}
		
	@Override
	public Intervention getIntervention(Device device) throws ServiceException {
		try {
			return database.getIntervention(device);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
