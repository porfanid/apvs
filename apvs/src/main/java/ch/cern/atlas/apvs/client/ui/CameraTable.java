package ch.cern.atlas.apvs.client.ui;

import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.domain.InterventionMap;
import ch.cern.atlas.apvs.client.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class CameraTable extends DockPanel implements Module {

	private FlexTable table = new FlexTable();
	private PtuSettings settings;
	private InterventionMap interventions;
	private List<String> ptuIds;
	
	private UpdateScheduler scheduler = new UpdateScheduler(this);

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {
		
		add(table, CENTER);
		
		RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		
		InterventionMapChangedRemoteEvent.subscribe(eventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedRemoteEvent event) {
						interventions = event.getInterventionMap();

						ptuIds = interventions.getPtuIds();

						configChanged();
						scheduler.update();
					}

				});

		PtuSettingsChangedRemoteEvent.subscribe(eventBus,
				new PtuSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedRemoteEvent event) {
						settings = event.getPtuSettings();
						configChanged();
						scheduler.update();
					}
				});

		
		return true;
	}
	
	@Override
	public boolean update() {
		return false;
	}

	private void configChanged() {
		table.clear();
		
		if ((ptuIds == null) || (settings == null)) {
			return;
		}
		
		int row = 0;
		int column = 0;
		for(String ptuId: ptuIds) {
			table.setWidget(row, column, new Label(ptuId));
			table.getFlexCellFormatter().setColSpan(row, column, 1);
			table.setWidget(row+1, column++, new ImageView(settings.getCameraUrl(ptuId, CameraView.HELMET)));
			table.setWidget(row+1, column++, new ImageView(settings.getCameraUrl(ptuId, CameraView.HAND)));
			if (column >= 3) {
				column = 0;
				row++;
				row++;
			}
		}
	}
}
