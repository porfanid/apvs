package ch.cern.atlas.apvs.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PingServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see ch.cern.atlas.apvs.client.PingService
     */
    RequestBuilder ping( AsyncCallback<Void> callback );

    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static PingServiceAsync instance;

        public static final PingServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (PingServiceAsync) GWT.create( PingService.class );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
