package ch.cern.atlas.apvs.ptu.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

public class PtuPushHandler extends PtuReconnectHandler {

	private Map<Channel, List<PtuSimulator>> simulators = new HashMap<Channel, List<PtuSimulator>>();
	
	public PtuPushHandler(ClientBootstrap bootstrap) {
		super(bootstrap);
		
		init();
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
		
		String[] ptuIds = { "PTU_78", "PTU_82", "PTU_37", "PTU_27", "PTU_39", "PTU_88" };
		List<PtuSimulator> listOfSimulators = new ArrayList<PtuSimulator>(ptuIds.length);
		for (int i = 0; i < ptuIds.length; i++) {
			String ptuId = ptuIds[i];

			PtuSimulator simulator = new PtuSimulator(e.getChannel(), ptuId, false);
			listOfSimulators.add(simulator);
			simulator.start();
		}

		simulators.put(e.getChannel(), listOfSimulators);	
	}
	
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
    		ChannelStateEvent e) throws Exception {
    	super.channelDisconnected(ctx, e);

    	List<PtuSimulator> listOfSimulators = simulators.get(e.getChannel());
    	if (listOfSimulators != null) {
    		System.err.println("Interrupting Threads...");
    		for (Iterator<PtuSimulator> i = listOfSimulators.iterator(); i.hasNext(); ) {
    			i.next().interrupt();
    		}
    		simulators.remove(e.getChannel());
    	}   	
    }

	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// handle closed connection
		init();

		super.channelClosed(ctx, e);
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		super.messageReceived(ctx, e);
	}
	
	private void init() {
	}
}
