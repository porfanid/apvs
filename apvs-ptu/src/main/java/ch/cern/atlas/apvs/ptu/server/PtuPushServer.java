package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class PtuPushServer {

    private final String host;
    private final int port;

    public PtuPushServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        PtuPushHandler handler = new PtuPushHandler(bootstrap);
        
        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new PtuPipelineFactory(handler));
        
        // Start the connection attempt.
        handler.connect(new InetSocketAddress(host, port));
    }

    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + PtuPushServer.class.getSimpleName() +
                    " <host> <port>");
            return;
        }

        // Parse options.
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        new PtuPushServer(host, port).run();
    }
}
