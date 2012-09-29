package ch.cern.atlas.apvs.ptu.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class PtuConnectPushHandler  {

	private final Logger log = Logger.getLogger(getClass().getName());

	private String[] ptuIds = { "PTU_78347", "PTU_82098", "PTU_37309",
			"PTU_27372", "PTU_39400", "PTU_88982" };

	public PtuConnectPushHandler() {
	}
	

	public void run(final String host, final int port, final int refresh) {
		PtuSimulator simulator = new PtuSimulator("PTU1234", refresh) {
			@Override
			protected synchronized OutputStream sendBufferAndClear(
					OutputStream os) {
				ByteArrayOutputStream baos = (ByteArrayOutputStream)os;
					
				try {
					Socket socket = new Socket(host, port);
//					baos.writeTo(socket.getOutputStream());
					socket.getOutputStream().write(baos.toByteArray(), 0, baos.size()-1);
					socket.close();
				} catch (UnknownHostException e) {
					log.info("Error "+e);
				} catch (IOException e) {
					log.info("Error "+e);
				}
				
				return super.sendBufferAndClear(os);
			}
		};
		simulator.start();
	}
}
