package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;

public class PtuServer {

	public static void main(String[] args) throws NumberFormatException, IOException {
		switch (args.length) {
		case 1:
			new PtuPullServer(Integer.parseInt(args[0])).run();
			break;
		case 2:
			new PtuPushServer(args[0], Integer.parseInt(args[1])).run();
			break;
		default:
			System.err.println("Usage: " + PtuServer.class.getSimpleName()
					+ " [<host>] <port>");
			return;
		}
	}
}