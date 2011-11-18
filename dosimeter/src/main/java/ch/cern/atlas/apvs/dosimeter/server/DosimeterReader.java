package ch.cern.atlas.apvs.dosimeter.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.cern.atlas.apvs.domain.Dosimeter;

public class DosimeterReader implements Runnable {

	private Socket socket;
	private Map<Integer, Dosimeter> dosimeters = new HashMap<Integer, Dosimeter>();

	public DosimeterReader(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
		while (true) {
			try {
				BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line;
				while ((line = is.readLine()) != null) {
					Dosimeter dosimeter = DosimeterCoder.decode(line);
					dosimeters.put(dosimeter.getSerialNo(), dosimeter);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Set<Integer> getDosimeterSerialNumbers() {
		return dosimeters.keySet();
	}
	
	public Dosimeter getDosimeter(int serialNo) {
		return dosimeters.get(serialNo);
	}
}
