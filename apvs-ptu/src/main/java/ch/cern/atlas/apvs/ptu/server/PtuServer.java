package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;

public class PtuServer {

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		if (args.length < 2) {
			System.err.println("Usage: " + PtuServer.class.getSimpleName()
					+ "[<host>] <port> <refresh> [IDs...]");
			return;
		}

		try {
			// is arg 0 port number ?
			int port = Integer.parseInt(args[0]);
			String[] ids = null;
			if (args.length > 2) {
				ids = new String[args.length - 2];
				System.arraycopy(args, 2, ids, 0, ids.length);
			}
			new PtuPullServer(port, Integer.parseInt(args[1]), ids).run();
		} catch (NumberFormatException e) {

			// arg 0 is host
			String[] ids = null;
			if (args.length > 3) {
				ids = new String[args.length - 3];
				System.arraycopy(args, 3, ids, 0, ids.length);
			}
			new PtuPushServer(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), ids).run();
		}
	}
}