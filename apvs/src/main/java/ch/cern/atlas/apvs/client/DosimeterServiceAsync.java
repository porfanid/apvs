package ch.cern.atlas.apvs.client;

import java.util.Set;

import ch.cern.atlas.apvs.dosimeter.server.Dosimeter;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Mark Donszelmann
 */
public interface DosimeterServiceAsync {
    
	public void getSerialNumbers(AsyncCallback<Set<Integer>> callback);

    public void getDosimeter(int serialNo, int currentHashCode, AsyncCallback<Dosimeter> callback);

}
