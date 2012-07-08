package ch.cern.atlas.apvs.ptu.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;


public class PtuServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(
            PtuServerHandler.class.getName());

	private final boolean json;

    public PtuServerHandler(boolean json) {
		this.json = json;
	}
    
    @Override
    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	System.err.println("Connected from "+e.getChannel().getRemoteAddress());
    	
    	Thread thread = new Thread(new PtuSimulator(e.getChannel(), json));
    	thread.start();
    	
    	super.channelConnected(ctx, e);
    }
    
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
    		ChannelStateEvent e) throws Exception {
    	System.err.println("Disconnected from "+e.getChannel().getRemoteAddress());
    	super.channelDisconnected(ctx, e);
    }
    
    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) {

    	String request = (String) e.getMessage();
    	System.err.println(request);
    	String response = request;
    	e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.",
                e.getCause());
        e.getChannel().close();
    }
}
