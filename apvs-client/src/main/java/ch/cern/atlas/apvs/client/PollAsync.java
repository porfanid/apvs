package ch.cern.atlas.apvs.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Mark Donszelmann
 */
public interface PollAsync {

    public void pollDelayed(int milli, AsyncCallback<Event> callback);
}
