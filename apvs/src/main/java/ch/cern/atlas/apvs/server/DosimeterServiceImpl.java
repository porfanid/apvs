package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.atmosphere.gwt.poll.AtmospherePollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.DosimeterService;
import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterReader;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DosimeterServiceImpl extends AtmospherePollService
        implements DosimeterService {
	
	Socket socket;
	DosimeterReader dosimeterReader;
	
	public DosimeterServiceImpl() {
		super();
		System.err.println("Pollservice created");
	}
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		System.err.println("Pollservice init");

		System.err.println("Pollservice initServlet");
		
		try {
			socket = new Socket("localhost", 4001);
			
			dosimeterReader = new DosimeterReader(socket);
			Thread thread = new Thread(dosimeterReader);
			thread.start();
		} catch (UnknownHostException e) {
			throw new ServletException(e); 
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		System.err.println("Pollservice destroy");
		
		try {
			socket.shutdownInput();
			socket.close();
		} catch (IOException e) {
			System.err.println("Could not close socket");
		}
	}
	
	@Override
	public Set<Integer> getSerialNumbers() {
		return dosimeterReader == null ? null : new HashSet<Integer>(dosimeterReader.getDosimeterSerialNumbers());
	}
	
	@Override
	public Dosimeter getDosimeter(int serialNo, int currentHashCode) {
		// TODO handle hashcode
		if (dosimeterReader == null) return null;
		return dosimeterReader.getDosimeter(serialNo);
	}

	/*
    @Override
    public Event pollDelayed(final int milli) {

        final SuspendInfo info = suspend();

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    info.writeAndResume(new Event(milli, "Polling: Delayed event"));
                    for (Iterator<Integer> i = dosimeterReader.getDosimeterSerialNumbers().iterator(); i.hasNext(); ) {
                    	System.err.println(dosimeterReader.getDosimeter(i.next()));
                    }
                } catch (IOException e) {
                    logger.error("Failed to write and resume", e);
                }
            }
        }, milli);

        return null;
    }
*/

    private Logger logger = LoggerFactory.getLogger(DosimeterServiceImpl.class.getName());
}
