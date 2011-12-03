package ch.cern.atlas.apvs.client.service;

import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.domain.Dosimeter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsDosimeter")
public interface DosimeterService extends RemoteService {
	
	public List<Integer> getSerialNumbers();

    public Dosimeter getDosimeter(int serialNo);
    
	public Map<Integer, Dosimeter> getDosimeterMap();
}
