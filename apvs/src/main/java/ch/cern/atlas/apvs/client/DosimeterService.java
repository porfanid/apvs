package ch.cern.atlas.apvs.client;

import java.util.Set;

import ch.cern.atlas.apvs.domain.Dosimeter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsDosimeter")
public interface DosimeterService extends RemoteService {
	
	public Set<Integer> getSerialNumbers();

    public Dosimeter getDosimeter(int serialNo, int currentHashCode);
}
