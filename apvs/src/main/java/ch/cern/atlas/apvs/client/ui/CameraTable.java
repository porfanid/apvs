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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
		int labelColumn = 0;
		for(String ptuId: ptuIds) {
/* FIXME no labels as in Safari this sets the with of the column
			Label label = new Label(ptuId);
			label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			table.setWidget(row, labelColumn, label);
			table.getFlexCellFormatter().setColSpan(row, labelColumn, 2);
			labelColumn++;
*/
			Widget helmet = new ImageView(settings.getCameraUrl(ptuId, CameraView.HELMET));
//			helmet.setWidth(width+"px");
			table.setWidget(row, column++, helmet);
// row+1 ipv row			
			Widget hand = new ImageView(settings.getCameraUrl(ptuId, CameraView.HAND));
//			hand.setWidth(width+"px");
			table.setWidget(row, column++, hand);
// row+1 ipv row
			
			if (column >= 3) {
				column = 0;
				labelColumn = 0;
				row++;
//				row++;
			}
		}
	}
}
