package ch.cern.atlas.apvs.dosimeter.server;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;


public class DosimeterServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(
            DosimeterServerHandler.class.getName());

	private Map<Channel, DosimeterSimulator> simulators = new HashMap<Channel, DosimeterSimulator>();

    public DosimeterServerHandler() {
	}
    
    @Override
    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	System.err.println("Connected from "+e.getChannel().getRemoteAddress());
    	
    	DosimeterSimulator simulator = new DosimeterSimulator(e.getChannel());
    	simulators.put(e.getChannel(), simulator);
    	simulator.start();
    	
    	super.channelConnected(ctx, e);
    }
    
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
    		ChannelStateEvent e) throws Exception {
    	System.err.println("Disconnected from "+e.getChannel().getRemoteAddress());
    	DosimeterSimulator simulator = simulators.get(e.getChannel());
    	if (simulator != null) {
    		System.err.println("Interrupting Thread...");
    		simulators.remove(e.getChannel());
    		simulator.interrupt();
    	}
    	
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
