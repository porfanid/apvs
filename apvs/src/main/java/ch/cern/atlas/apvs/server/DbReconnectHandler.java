package ch.cern.atlas.apvs.server;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbReconnectHandler extends DbCallback {
	private static final int RECONNECT_DELAY = 20;
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private String url;
	private Timer timer;
	private boolean reconnectNow;
	
	public DbReconnectHandler() {
	}

	@Override
	public void dbDisconnected() throws SQLException {
		// handle reconnection
		if (reconnectNow) {
			log.info("Immediate Reconnecting to DB on " + url);
			super.connect(url);
			reconnectNow = false;
		} else {
			log.info("Sleeping for: " + RECONNECT_DELAY + "s");
			timer = new HashedWheelTimer();
			timer.newTimeout(new TimerTask() {
				public void run(Timeout timeout) throws Exception {
					log.info("Reconnecting to DB on " + url);
					DbReconnectHandler.super.connect(url);
				}
			}, RECONNECT_DELAY, TimeUnit.SECONDS);
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

		if (timer != null) {
			timer.stop();
			timer = null;
		}

		super.disconnect();
	}
}
