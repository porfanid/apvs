package ch.cern.atlas.apvs.server;

import org.atmosphere.gwt.poll.AtmospherePollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.Event;
import ch.cern.atlas.apvs.client.Poll;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class PollService extends AtmospherePollService
        implements Poll {

    @Override
    public Event pollDelayed(final int milli) {

        final SuspendInfo info = suspend();

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    info.writeAndResume(new Event(milli, "Polling: Delayed event"));
                } catch (IOException e) {
                    logger.error("Failed to write and resume", e);
                }
            }
        }, milli);

        return null;
    }

    private Logger logger = LoggerFactory.getLogger(PollService.class.getName());
}
