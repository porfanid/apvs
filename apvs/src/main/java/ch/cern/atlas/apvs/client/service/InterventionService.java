package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Intervention;
import ch.cern.atlas.apvs.domain.SortOrder;
import ch.cern.atlas.apvs.domain.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsIntervention")
public interface InterventionService extends TableService<Intervention>, RemoteService {

	Device addDevice(Device device) throws ServiceException;

	User addUser(User user) throws ServiceException;

	Intervention addIntervention(Intervention intervention) throws ServiceException;

	Intervention updateIntervention(Intervention intervention) throws ServiceException;

	Intervention getIntervention(Device device) throws ServiceException;

	List<User> getUsers(boolean notBusy) throws ServiceException;

	List<Device> getDevices(boolean notBusy) throws ServiceException;
	
	long getRowCount(boolean showTest) throws ServiceException;

	List<Intervention> getTableData(int start, int length, List<SortOrder> order, boolean showTest)
			throws ServiceException;

}
