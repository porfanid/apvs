package ch.cern.atlas.apvs.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBusIdsChangedEvent;

import com.google.gwt.user.client.ui.TextBox;

public class WorkerId extends TextBox {

	private Integer ptuId;
	private Map<Integer, Long> workerIdByPtuId = new HashMap<Integer, Long>();
	private Map<Long, Integer> ptuIdByWorkerId = new HashMap<Long, Integer>();

	public WorkerId(RemoteEventBus remoteEventBus,
			final RemoteEventBus localEventBus) {

		setEnabled(false);

		SelectPtuEvent.subscribe(remoteEventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				Integer remotePtuId = event.getPtuId();
				Long workerId = event.getEventBusUUID();

				if (remotePtuId != null) {
					Integer oldRemotePtuId = ptuIdByWorkerId.put(workerId, remotePtuId);
					if (oldRemotePtuId != null) {
						workerIdByPtuId.remove(oldRemotePtuId);
					}
					workerIdByPtuId.put(remotePtuId, workerId);
				} else {
					Integer oldRemotePtuId = ptuIdByWorkerId.get(workerId);
					if (oldRemotePtuId != null) {
						workerIdByPtuId.remove(oldRemotePtuId);
						ptuIdByWorkerId.remove(workerId);
					}
				}

				update();
			}
		});

		SelectPtuEvent.subscribe(localEventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				ptuId = event.getPtuId();

				update();
			}
		});
	}

	private Long getWorkerId(Integer ptuId) {
		return ptuId != null ? workerIdByPtuId.get(ptuId) : null;
	}

	private void update() {
		Long workerId = getWorkerId(ptuId);
		setText(workerId != null ? Long.toString(workerId) : "");
	}
}
