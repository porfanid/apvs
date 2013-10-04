package ch.cern.atlas.apvs.client.ui;

import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.HistoryChangedEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.InterventionMap;
import ch.cern.atlas.apvs.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CameraTable extends SimplePanel implements Module {

	private FlexTable table = new FlexTable();
	private PtuSettings settings;
	private InterventionMap interventions;
	private History history;
	private List<Device> ptus;
	
	private UpdateScheduler scheduler = new UpdateScheduler(this);
	
	private ClientFactory factory;

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {
		
		this.factory = clientFactory;
		
		table.setWidth("100%");
		add(table);
				
		RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		
		InterventionMapChangedRemoteEvent.subscribe(eventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedRemoteEvent event) {
						interventions = event.getInterventionMap();

						ptus = interventions.getPtus();

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
		
		HistoryChangedEvent.subscribe(clientFactory,
				new HistoryChangedEvent.Handler() {

					@Override
					public void onHistoryChanged(HistoryChangedEvent event) {
						history = event.getHistory();
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
		
		if ((ptus == null) || (settings == null) || (history == null)) {
			return;
		}
		
		int row = 0;
		int column = 0;
		int labelColumn = 0;
		for(Device ptu: ptus) {

			Label label = new Label(ptu.getName());
			label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			table.setWidget(row, labelColumn, label);
			// 2
			table.getFlexCellFormatter().setColSpan(row, labelColumn, 1);
			labelColumn++;

			Widget helmet = new ImageView(factory, settings.getCameraUrl(ptu.getName(), CameraView.HELMET, factory.getProxy()));
			table.setWidget(row+1, column, helmet);
			// 25%
			table.getCellFormatter().setWidth(row+1, column, "50%");
			
//			SpecificTimeView timeView = new SpecificTimeView();
//			Chart chart = timeView.createSingleChart(factory, "DoseRate", ptu, historyMap, interventions, false);
//			Label chart = new Label("TEST "+(row+2)+" "+column+" "+ptu.getName());
//			table.setWidget(row+2, column, chart);
			// 25%
//			table.getCellFormatter().setWidth(row+2, column, "50%");

			column++;

//			Widget hand = new ImageView(factory, settings.getCameraUrl(ptu, CameraView.HAND, factory.getProxy()));
//			table.setWidget(row+1, column, hand);
//			table.getCellFormatter().setWidth(row+1, column, "25%");
//			column++;
			
			// 3
			if (column >= 2) {
				column = 0;
				labelColumn = 0;
				row += 2;
			}
		}
	}
}
