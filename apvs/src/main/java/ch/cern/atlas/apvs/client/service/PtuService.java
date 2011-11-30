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
	
	public List<Integer> getPtuIds(long currentHashCode);
	
	public Ptu getPtu(int ptuId, long currentHashCode);
	
	public Measurement<Double> getLastMeasurement(long currentHashCode);
	
	public Measurement<Double> getMeasurement(int putId, String name, long currentHashCode);
	
	// single call
    public List<Measurement<Double>> getCurrentMeasurements();
    
}
