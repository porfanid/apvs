package ch.cern.atlas.apvs.dosimeter.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DosimeterServlet extends HttpServlet {
	
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {

    	response.setContentType("text/plain");
    	response.setStatus(HttpServletResponse.SC_OK);
    	
    	response.setBufferSize(0);
        ServletOutputStream out = response.getOutputStream();
        for (int i=0; i<10000; i++) {
        	out.println( "DosimeterServlet Executed "+request.getSession(true).getId().hashCode() );
        	out.flush();
        	response.flushBuffer(); 
        	try {
        		Thread.sleep(20000);
        	} catch (InterruptedException e) {
        		// ignored
        	}
        }
        out.close();
    }
}

