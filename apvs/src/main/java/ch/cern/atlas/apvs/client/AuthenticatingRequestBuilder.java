package ch.cern.atlas.apvs.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;

public class AuthenticatingRequestBuilder extends RpcRequestBuilder {

	public AuthenticatingRequestBuilder() {
	}

	/** 
	 * Workaround for #329, http://stackoverflow.com/questions/12506897/is-safari-on-ios-6-caching-ajax-results
	 * iOS 6.0.x caches post reuqests and thus this may give problems doing RPC calls.
	 * 
	 * for every service do a
	 * 
	 * ((ServiceDefTarget)eventBusService).setRpcRequestBuilder(new AuthenticatingRequestBuilder());
	 * 
	 */
    @Override
    protected RequestBuilder doCreate(String serviceEntryPoint) 
    {
            RequestBuilder requestBuilder = super.doCreate(serviceEntryPoint);           
            requestBuilder.setHeader("Cache-Control", "no-cache");

            return requestBuilder;
    }
}
