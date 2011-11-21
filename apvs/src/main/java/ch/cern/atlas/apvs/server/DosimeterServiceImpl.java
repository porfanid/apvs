package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.DosimeterService;
import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterReader;
import ch.cern.atlas.apvs.server.ResponseHandler.Response;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DosimeterServiceImpl extends ResponsePollService implements
		DosimeterService {

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
			dosimeterReader.addValueChangeHandler(getSerialNumbersResponseHandler);
			dosimeterReader.addValueChangeHandler(getDosimeterResponseHandler);
			dosimeterReader.addValueChangeHandler(getDosimeterMapResponseHandler);
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

	private ResponseHandler<Map<Integer, Dosimeter>> getSerialNumbersResponseHandler = new ResponseHandler<Map<Integer, Dosimeter>>(this);

	@Override
	public Set<Integer> getSerialNumbers(int currentHashCode) {
		return getSerialNumbersResponseHandler.respond(currentHashCode, new Response<Set<Integer>>() {
			
			@Override
			public Set<Integer> getValue() {
				return dosimeterReader.getDosimeterSerialNumbers();
			}
		});		
	}

	private ResponseHandler<Map<Integer, Dosimeter>> getDosimeterResponseHandler = new ResponseHandler<Map<Integer, Dosimeter>>(this);

	@Override
	public Dosimeter getDosimeter(final int serialNo, int currentHashCode) {
		return getDosimeterMapResponseHandler.respond(currentHashCode, new Response<Dosimeter>() {
			
			@Override
			public Dosimeter getValue() {
				return dosimeterReader.getDosimeter(serialNo);
			}
		});		
	}
	
	private ResponseHandler<Map<Integer, Dosimeter>> getDosimeterMapResponseHandler = new ResponseHandler<Map<Integer, Dosimeter>>(this);
	
	@Override
	public Map<Integer, Dosimeter> getDosimeterMap(int currentHashCode) {
		return getDosimeterMapResponseHandler.respond(currentHashCode, new Response<Map<Integer, Dosimeter>>() {
			
			@Override
			public Map<Integer, Dosimeter> getValue() {
				return dosimeterReader.getDosimeterMap();
			}
		});		
	}

}
