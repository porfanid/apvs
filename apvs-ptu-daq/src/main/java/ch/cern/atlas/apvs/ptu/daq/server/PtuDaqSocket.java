package ch.cern.atlas.apvs.ptu.daq.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PtuDaqSocket implements PtuDaqListener {

	private BufferedWriter os;

	public PtuDaqSocket(Socket socket, PtuDaqQueue queue) throws IOException {
		System.out.print("PTU DAQ Server connected on: "
				+ socket.getInetAddress());
		os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		queue.addListener(this);
	}
	
	@Override
	public void itemAvailable(String item) {
		try {
		os.write(item);
		os.newLine();
		os.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
