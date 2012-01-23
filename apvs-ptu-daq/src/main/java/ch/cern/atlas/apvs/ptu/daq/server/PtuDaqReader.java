package ch.cern.atlas.apvs.ptu.daq.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.cern.atlas.apvs.ptu.server.BodyTemperature;
import ch.cern.atlas.apvs.ptu.server.CO2;
import ch.cern.atlas.apvs.ptu.server.Humidity;
import ch.cern.atlas.apvs.ptu.server.O2;
import ch.cern.atlas.apvs.ptu.server.PtuJsonWriter;
import ch.cern.atlas.apvs.ptu.server.Temperature;

public class PtuDaqReader extends Thread {

	private static final long delay = 15000;

	private String hostName;
	private InetAddress address;
	private int portNo;
	private PtuDaqQueue queue;

	public PtuDaqReader(String hostName, int portNo, PtuDaqQueue queue)
			throws UnknownHostException {
		this.hostName = hostName;
		address = InetAddress.getByName(hostName);
		this.portNo = portNo;
		this.queue = queue;
	}

	public int getPtuId() {
		return Math.abs((address.getHostAddress() + ":" + portNo).hashCode());
	}

	@Override
	public void run() {
		Socket socket;
		System.err.println("Reading from " + hostName + " "
				+ address.getHostAddress() + ":" + portNo + " ptuId "
				+ getPtuId());

		while (true) {
			try {
				socket = new Socket(address, portNo);

				BufferedReader bis = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				String line;
				while ((line = bis.readLine()) != null) {
					System.out.println(line);
					try {
						queue.add(convert(line));
					} catch (ConversionException e) {
						System.err.println(e);
					}
				}

				bis.close();
			} catch (IOException e) {
				System.err.println("PtuId: " + getPtuId() + " Error " + e);
				System.err.println("Trying again in " + (delay / 1000.0)
						+ " seconds.");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e1) {
					// ignored
				}
			}
		}
	}

	private String convert(String line) throws ConversionException {
		Pattern pattern = Pattern
				.compile("^\\$YXS(\\D+),(\\d+),(.*)\\*([0-9A-Fa-f]+)");
		Matcher matcher = pattern.matcher(line);

		if (!matcher.matches()) {
			throw new ConversionException(line);
		}

		String name = matcher.group(1);
		@SuppressWarnings("unused")
		String id = matcher.group(2);
		String[] values = matcher.group(3).split(",");
		@SuppressWarnings("unused")
		String checksum = matcher.group(4);

		StringBuffer s = new StringBuffer();
		Date d = new Date();
		double v = Double.parseDouble(values[0]);
		int ptuId = getPtuId();
		if (name.equals("O2")) {
			s.append(PtuJsonWriter.toJson(new O2(ptuId, v, d)));
		} else if (name.equals("CO")) {
			s.append(PtuJsonWriter.toJson(new CO2(ptuId, v, d)));
		} else if (name.equals("HT")) {
			s.append(PtuJsonWriter.toJson(new Temperature(ptuId, v, d)));
			s.append(PtuJsonWriter.toJson(new Humidity(ptuId, Double
					.parseDouble(values[1]), d)));
		} else if (name.equals("BT")) {
			s.append(PtuJsonWriter.toJson(new BodyTemperature(ptuId, v, d)));
		} else {
			throw new ConversionException(name + " " + line);
		}

		return s.toString();
	}
}
