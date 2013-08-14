package ch.cern.atlas.apvs.client.service;

import java.util.Date;
import java.util.List;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Order;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPtu")
public interface PtuService extends RemoteService {
	public List<Measurement> getMeasurements(List<Device> ptuList, String name) throws ServiceException;
	public List<Measurement> getMeasurements(Device device, String name) throws ServiceException;
	
	public History getHistory(List<Device> devices, Date from, Integer maxEntries) throws ServiceException;

	public void handleOrder(Order order) throws ServiceException;
	
	public void clearPanicAlarm(Device device) throws ServiceException;
	public void clearDoseAlarm(Device device) throws ServiceException;
	public void clearFallAlarm(Device device) throws ServiceException;
}
