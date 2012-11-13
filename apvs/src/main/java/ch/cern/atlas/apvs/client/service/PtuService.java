package ch.cern.atlas.apvs.client.service;

import java.util.List;

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
	public List<Measurement> getMeasurements(String ptuId, String name) throws ServiceException;
	public History getHistory(String ptuId, String name) throws ServiceException;
	public void handleOrder(Order order) throws ServiceException;
}
