package ch.cern.atlas.apvs.ptu.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;


public class PtuServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(
            PtuServerHandler.class.getName());

	private final boolean json;
	private Map<Channel, List<PtuSimulator>> simulators = new HashMap<Channel, List<PtuSimulator>>();

    public PtuServerHandler(boolean json) {
		this.json = json;
	}
    
    @Override
    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	System.err.println("Connected from "+e.getChannel().getRemoteAddress());

    	int noOfPtus = 6;
    	
		int[] ptuIds = { 78347, 82098, 37309, 27372, 39400, 88982 };
		List<PtuSimulator> listOfSimulators = new ArrayList<PtuSimulator>(noOfPtus);
		for (int i = 0; i < noOfPtus; i++) {
			int ptuId = ptuIds[i];

			PtuSimulator simulator = new PtuSimulator(e.getChannel(), ptuId, true, json);
			listOfSimulators.add(simulator);
			simulator.start();
		}

		simulators.put(e.getChannel(), listOfSimulators);
		
    	super.channelConnected(ctx, e);
    }
    
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
    		ChannelStateEvent e) throws Exception {
    	System.err.println("Disconnected from "+e.getChannel().getRemoteAddress());
    	List<PtuSimulator> listOfSimulators = simulators.get(e.getChannel());
    	if (listOfSimulators != null) {
    		System.err.println("Interrupting Threads...");
    		for (Iterator<PtuSimulator> i = listOfSimulators.iterator(); i.hasNext(); ) {
    			i.next().interrupt();
    		}
    		simulators.remove(e.getChannel());
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
