package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPtu")
public interface PtuService extends RemoteService {
	
	public List<Integer> getPtuIds();
	
	public Ptu getPtu(int ptuId);
	
	public Measurement<Double> getMeasurement(int putId, String name);
	
    public List<Measurement<Double>> getCurrentMeasurements();
    
}
