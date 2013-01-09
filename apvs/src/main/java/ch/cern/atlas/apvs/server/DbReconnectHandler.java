package ch.cern.atlas.apvs.server;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

public class DbReconnectHandler extends DbCallback {
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private String url;
	private boolean reconnectNow;
	
	public DbReconnectHandler(RemoteEventBus eventBus) {
		super(eventBus);
	}

	@Override
	public void dbDisconnected() throws SQLException {
		// handle reconnection
		if (reconnectNow) {
			log.info("Immediate Reconnecting to DB on " + url);
			super.connect(url);
			reconnectNow = false;
		}

		super.dbDisconnected();
	}

	public void exceptionCaught(Exception e) {
		log.warn("Exception", e);
		super.exceptionCaught(e);
	}

	public void connect(String newUrl) {
		if (newUrl.equals(url))
			return;

		url = newUrl;

		if (isConnected()) {
			reconnect(true);
		} else {
			super.connect(url);
		}
	}

	public void reconnect(boolean reconnectNow) {
		this.reconnectNow = reconnectNow;
		super.disconnect();
	}
}
