package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;

public class PtuServer {

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		if (args.length == 0) {
			System.err.println("Usage: " + PtuServer.class.getSimpleName()
					+ " [<host>] <port> [IDs...]");
			return;
		}

		try {
			// is arg 0 port number ?
			int port = Integer.parseInt(args[0]);
			String[] ids = null;
			if (args.length > 1) {
				ids = new String[args.length - 1];
				System.arraycopy(args, 1, ids, 0, ids.length);
			}
			new PtuPullServer(port, ids).run();
		} catch (NumberFormatException e) {
			// arg 0 is host
			String[] ids = null;
			if (args.length > 2) {
				ids = new String[args.length - 2];
				System.arraycopy(args, 2, ids, 0, ids.length);
			}
			new PtuPushServer(args[0], Integer.parseInt(args[1]), ids).run();
		}

	}
}