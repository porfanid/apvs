package ch.cern.atlas.apvs.client.service;

import java.util.Date;
import java.util.List;

import ch.cern.atlas.apvs.client.domain.Intervention;
import ch.cern.atlas.apvs.client.domain.User;
import ch.cern.atlas.apvs.domain.Device;

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

	void addIntervention(Intervention intervention) throws ServiceException;

	void endIntervention(int id, Date date) throws ServiceException;

	void updateInterventionImpactNumber(int id, String impactNumber)
			throws ServiceException;

	void updateInterventionDescription(int id, String description)
			throws ServiceException;

	Intervention getIntervention(String ptuId) throws ServiceException;

	List<User> getUsers(boolean notBusy) throws ServiceException;

	List<Device> getDevices(boolean notBusy) throws ServiceException;

}
