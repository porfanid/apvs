package ch.cern.atlas.apvs.client.service;

import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPtu")
public interface PtuService extends RemoteService {
	public Ptu getPtu(int ptuId);
	public List<Measurement<Double>> getMeasurements(int ptuId, String name);
	public Map<Integer, List<Measurement<Double>>> getMeasurements(String name);
}
