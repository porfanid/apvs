package ch.cern.atlas.apvs.client.service;

import java.util.Map;

import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPtu")
public interface PtuService extends RemoteService {
	public Ptu getPtu(String ptuId);
	public Measurement getMeasurement(String ptuId, String name);
	public History getHistory(String ptuId, String name);
	public Map<String, History> getHistories(String name);	
}
