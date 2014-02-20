package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtuServer {

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		Logger log = LoggerFactory.getLogger(PtuServer.class.getName());

		if (args.length < 2) {
			log.info("Usage: " + PtuServer.class.getSimpleName()
					+ "[<host>] <port> <refresh> [IDs...]");
			return;
		}

		try {
			// is arg 0 port number ?
			int port = Integer.parseInt(args[0]);
			log.info("Creating pull server");
			
			String[] ids = null;
			if (args.length > 2) {
				ids = new String[args.length - 2];
				System.arraycopy(args, 2, ids, 0, ids.length);
			}
			new PtuPullServer(port, Integer.parseInt(args[1]), ids).run();
		} catch (NumberFormatException e) {

			log.info("Creating push server");
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