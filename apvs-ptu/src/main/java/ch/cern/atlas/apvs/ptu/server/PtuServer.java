package ch.cern.atlas.apvs.ptu.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class PtuServer {

    private final int port;
	private final boolean json;

    public PtuServer(int port, boolean json) {
        this.port = port;
        this.json = json;
    }

    public void run() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new PtuPipelineFactory(new PtuServerHandler(json)));

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
        
		System.out.println("PTU Demo Server open at "+port);
    }

    public static void main(String[] args) {
		boolean json = args.length > 0 ? args[0].equalsIgnoreCase("xml") ? false : true : true;
        new PtuServer(4005, json).run();
    }
}