package ch.cern.atlas.apvs.server;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.domain.Device;
import ch.cern.atlas.apvs.client.domain.Intervention;
import ch.cern.atlas.apvs.client.domain.User;
import ch.cern.atlas.apvs.client.service.InterventionService;
import ch.cern.atlas.apvs.client.service.ServiceException;
import ch.cern.atlas.apvs.client.service.SortOrder;

import com.google.gwt.view.client.Range;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class InterventionServiceImpl extends ResponsePollService implements
		InterventionService {

	private DbHandler dbHandler;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		dbHandler = DbHandler.getInstance();
	}
	
	@Override
	public int getRowCount() throws ServiceException {
		try {
			return dbHandler.getInterventionCount();
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Intervention> getTableData(Range range, SortOrder[] order)
			throws ServiceException {
		try {
			return dbHandler.getInterventions(range, order);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void addUser(User user) throws ServiceException {
		try {
			dbHandler.addUser(user);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void addDevice(Device device)
			throws ServiceException {
		try {
			dbHandler.addDevice(device);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Device> getDevices(boolean notBusy) throws ServiceException {
		try {
			return dbHandler.getDevices(notBusy);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void addIntervention(Intervention intervention) throws ServiceException {
		try {
			dbHandler.addIntervention(intervention);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void endIntervention(int id, Date date) throws ServiceException {
		try {
			dbHandler.endIntervention(id, date);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<User> getUsers(boolean notBusy) throws ServiceException {
		try {
			return dbHandler.getUsers(notBusy);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void updateInterventionImpactNumber(int id, String impactNumber)
			throws ServiceException {
		try {
			dbHandler.updateInterventionImpactNumber(id, impactNumber);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void updateInterventionDescription(int id, String description)
			throws ServiceException {
		try {
			dbHandler.updateInterventionDescription(id, description);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public Intervention getIntervention(String ptuId) throws ServiceException {
		try {
			return dbHandler.getIntervention(ptuId);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

}
