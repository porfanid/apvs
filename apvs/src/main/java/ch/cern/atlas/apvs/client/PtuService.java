package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.domain.Measurement;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPtu")
public interface PtuService extends RemoteService {
	
//	public List<Integer> getPtuIds(int currentHashCode);
	
	public Measurement<Double> getMeasurement(int putId, String name, int currentHashCode);
	
//    public Ptu getPtu(int ptuId, int currentHashCode);
    
}
