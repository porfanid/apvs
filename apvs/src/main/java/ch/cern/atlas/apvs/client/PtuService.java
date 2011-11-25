package ch.cern.atlas.apvs.client;

import java.util.List;

import ch.cern.atlas.apvs.domain.Measurement;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPtu")
public interface PtuService extends RemoteService {
	
	public Measurement<Double> getLastMeasurement(int currentHashCode);
	
	public Measurement<Double> getMeasurement(int putId, String name, int currentHashCode);
	
	// single call
    public List<Measurement<Double>> getCurrentMeasurements();
    
}
