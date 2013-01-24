package ch.cern.atlas.apvs.client.service;

import java.util.Date;
import java.util.List;

import ch.cern.atlas.apvs.client.domain.HistoryMap;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Order;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPtu")
public interface PtuService extends RemoteService {
	public List<Measurement> getMeasurements(List<String> ptuIdList, String name) throws ServiceException;
	public List<Measurement> getMeasurements(String ptuId, String name) throws ServiceException;
	
	public HistoryMap getHistoryMap(List<String> ptuIdList, Date from) throws ServiceException;

	public void handleOrder(Order order) throws ServiceException;
	
	public void clearPanicAlarm(String ptuId) throws ServiceException;
	public void clearDoseAlarm(String ptuId) throws ServiceException;
	public void clearFallAlarm(String ptuId) throws ServiceException;
}
