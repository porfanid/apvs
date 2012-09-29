package ch.cern.atlas.apvs.ptu.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

public class PtuPushHandler extends PtuReconnectHandler {

	private final Logger log = Logger.getLogger(getClass().getName());

	private Map<Channel, List<PtuSimulator>> simulators = new HashMap<Channel, List<PtuSimulator>>();
	private String[] ptuIds = { "PTU_78347", "PTU_82098", "PTU_37309",
			"PTU_27372", "PTU_39400", "PTU_88982" };
	private final int refresh;

	public PtuPushHandler(ClientBootstrap bootstrap, String[] ids, int refresh) {
		super(bootstrap);

		if (ids != null) {
			ptuIds = ids;
		}
		
		this.refresh = refresh;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);

		List<PtuSimulator> listOfSimulators = new ArrayList<PtuSimulator>(
				ptuIds.length);
		for (int i = 0; i < ptuIds.length; i++) {
			String ptuId = ptuIds[i];

			PtuSimulator simulator = new PtuSimulator(ptuId, refresh, e.getChannel());
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
			log.info("Interrupting Threads...");
			for (Iterator<PtuSimulator> i = listOfSimulators.iterator(); i
					.hasNext();) {
				i.next().interrupt();
			}
			simulators.remove(e.getChannel());
		}
	}
}
