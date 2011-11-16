package ch.cern.atlas.apvs.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPoll")
public interface Poll extends RemoteService {

    public Event pollDelayed(int milli);
}
