package ch.cern.atlas.apvs.ptu.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtuPullServer {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
    private final int port;
	private final int refresh;
    private final String[] ids;

    public PtuPullServer(int port, int refresh, String[] ids) {
    	this.refresh = refresh;
        this.port = port;
        this.ids = ids;
    }

    public void run() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

		Timer timer = new HashedWheelTimer();
        
        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new PtuPipelineFactory(timer, new PtuServerHandler(refresh, ids)));

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
        
		log.info("PTU Pull Server open at "+port);
    }

    public static void main(String[] args) {
        new PtuPullServer(4005, 5000, null).run();
    }
}