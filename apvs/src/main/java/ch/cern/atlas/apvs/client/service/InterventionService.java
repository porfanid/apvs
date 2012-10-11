package ch.cern.atlas.apvs.client.service;

import java.util.Date;
import java.util.List;

import ch.cern.atlas.apvs.client.ui.Device;
import ch.cern.atlas.apvs.client.ui.Intervention;
import ch.cern.atlas.apvs.client.ui.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsIntervention")
public interface InterventionService extends TableService<Intervention>,
		RemoteService {

	void addDevice(Device device) throws ServiceException;

	void addUser(User user) throws ServiceException;

	void addIntervention(int userId, int deviceId, Date date, String string)
			throws ServiceException;

	void endIntervention(int id, Date date) throws ServiceException;

	void updateInterventionDescription(int id, String description)
			throws ServiceException;

	List<User> getUsers(boolean notBusy) throws ServiceException;

	List<Device> getDevices(boolean notBusy) throws ServiceException;

}
