package ch.cern.atlas.apvs.ptu.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtuServerHandler extends IdleStateAwareChannelUpstreamHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private Map<Channel, List<PtuSimulator>> simulators = new HashMap<Channel, List<PtuSimulator>>();
	private String[] ptuIds = { "PTU_78347", "PTU_82098", "PTU_37309",
			"PTU_27372", "PTU_39400", "PTU_88982" };

	private final int refresh;

	public PtuServerHandler(int refresh, String[] ids) {
		this.refresh = refresh;

		if (ids != null) {
			ptuIds = ids;
		}
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		log.info("Connected from " + e.getChannel().getRemoteAddress());

		List<PtuSimulator> listOfSimulators = new ArrayList<PtuSimulator>(
				ptuIds.length);
		for (int i = 0; i < ptuIds.length; i++) {
			String ptuId = ptuIds[i];

			PtuSimulator simulator = new PtuSimulator(ptuId, refresh,
					e.getChannel());
			listOfSimulators.add(simulator);
			simulator.start();
		}

		simulators.put(e.getChannel(), listOfSimulators);

		super.channelConnected(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		log.info("Disconnected from " + e.getChannel().getRemoteAddress());
		List<PtuSimulator> listOfSimulators = simulators.get(e.getChannel());
		if (listOfSimulators != null) {
			log.info("Interrupting Threads...");
			for (Iterator<PtuSimulator> i = listOfSimulators.iterator(); i
					.hasNext();) {
				i.next().interrupt();
			}
			simulators.remove(e.getChannel());
		}

		super.channelDisconnected(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

		String request = (String) e.getMessage();
		log.info(request);
		String response = request;
		e.getChannel().write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		// Close the connection when an exception is raised.
		log.warn("Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}
}
