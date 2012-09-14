package ch.cern.atlas.apvs.ptu.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class PtuPullServer {

    private final int port;
    private final String[] ids;

    public PtuPullServer(int port, String[] ids) {
        this.port = port;
        this.ids = ids;
    }

    public void run() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new PtuPipelineFactory(new PtuServerHandler(ids)));

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
        
		System.out.println("PTU Pull Server open at "+port);
    }

    public static void main(String[] args) {
        new PtuPullServer(4005, null).run();
    }
}