package ch.cern.atlas.apvs.eventbus.server;

import javax.servlet.http.HttpServletRequest;

public class ServerSerialization {

	private ServerSerialization() {
	}
	
	public static String getModuleBaseURL(
			HttpServletRequest request, String moduleBaseURL) {
        //get the base url from the header instead of the body this way 
        //apache reverse proxy with rewrite on the header can work
		
		// in apache config use:
		
	    // ProxyPass /app/ ajp://localhost:8009/App-0.0.1-SNAPSHOT/
		//
	    //    <Location /app/>
		//
	    //    RequestHeader edit X-GWT-Module-Base ^(.*)/app/(.*)$ $1/App-0.0.1-SNAPSHOT/$2
		//
	    //    </Location>
        String moduleBaseURLHdr = request.getHeader("X-GWT-Module-Base");

        if(moduleBaseURLHdr != null){
                moduleBaseURL = moduleBaseURLHdr;
        }

        return moduleBaseURL;
	}	
}
