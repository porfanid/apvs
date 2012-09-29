package ch.cern.atlas.apvs.dosimeter.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class DosimeterServer {

	private final Logger log = Logger.getLogger(getClass().getName());
	
	private final int port = 4001;

	public DosimeterServer() {
	}

	public void run() {
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new DosimeterServerPipelineFactory());

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));

		log.info("Dosimeter Demo Server open at " + port);
	}

	public static void main(String[] args) {
		new DosimeterServer().run();
	}
}