package ch.cern.atlas.apvs.client.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ui.Module;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class UpdateScheduler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private boolean updateScheduled = false;
	private Module module;

	public UpdateScheduler(Module module) {
		this.module = module;
	}

	public synchronized boolean update() {
		if (updateScheduled) {
			return false;
		}

		log.trace("Scheduling update for "+module.getClass());
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				log.trace("Executing update for "+module.getClass());
				updateScheduled = false;
				module.update();
			}
		});
		updateScheduled = true;
		return true;
	}
}
