package ch.cern.atlas.apvs.client.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.domain.Ternary;
import ch.cern.atlas.apvs.client.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.widget.ClickableHtmlColumn;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.client.widget.GlassPanel;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.domain.Alarm;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.EventChangedEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuClientConstants;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;

public class AlarmView extends GlassPanel implements Module {

	private CellTable<String> table = new CellTable<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();

	private EventBus cmdBus;

	private String ptuId;
	private Ternary daqOk = Ternary.Unknown;

	private boolean showHeader = true;

	private String options;
	private List<String> names = Arrays.asList(new String[] { "Panic Button",
			"Dose Rate", "Fall Detect" });
	private List<Class<?>> classes = Arrays.asList(new Class<?>[] {
			ButtonCell.class, ButtonCell.class, ButtonCell.class });

	// FIXME should be on server
	private Map<String, Alarm> alarmMap = new HashMap<String, Alarm>();

	private UpdateScheduler scheduler = new UpdateScheduler(this);

	public AlarmView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {

		RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		
		cmdBus = clientFactory.getEventBus(args.getArg(0));
		options = args.getArg(1);

		showHeader = !options.contains("NoHeader");

		add(table);

		// name column
		ClickableHtmlColumn<String> name = new ClickableHtmlColumn<String>() {
			@Override
			public String getValue(String name) {
				return name;
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		table.addColumn(name, showHeader ? new TextHeader("Name") : null);

		EditableCell cell = new EditableCell(classes, 50);
		cell.setDateFormat(PtuClientConstants.dateFormatNoSeconds);

		Column<String, Object> column = new Column<String, Object>(cell) {
			@Override
			public Object getValue(String name) {
				if (name.equals(names.get(0))) {
					return alarmMap.get(ptuId).isPanic() ? "ALARM" : "cleared";
				} else if (name.equals(names.get(1))) {
					return alarmMap.get(ptuId).isDose() ? "ALARM" : "cleared";
				} else if (name.equals(names.get(2))) {
					return alarmMap.get(ptuId).isFall() ? "ALARM" : "cleared";
				}
				System.out.println("AlarmView name unknown '" + name + "'");
				return "unknown";
			}

			@Override
			public void render(Context context, String name, SafeHtmlBuilder sb) {
				getCell().render(context, getValue(name), sb);
			}
		};
		column.setFieldUpdater(new FieldUpdater<String, Object>() {

			@Override
			public void update(int index, String name, Object value) {
				if (name.equals(names.get(0))) {
					alarmMap.get(ptuId).setPanic(false);
				} else if (name.equals(names.get(1))) {
					alarmMap.get(ptuId).setDose(false);
				} else if (name.equals(names.get(2))) {
					alarmMap.get(ptuId).setFall(false);
				}
				
				// Trigger change
			}
		});
		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.addColumn(column, showHeader ? new TextHeader("Value") : null);

		dataProvider.addDataDisplay(table);
		dataProvider.setList(names);

		ConnectionStatusChangedRemoteEvent.subscribe(
				clientFactory.getRemoteEventBus(),
				new ConnectionStatusChangedRemoteEvent.Handler() {

					@Override
					public void onConnectionStatusChanged(
							ConnectionStatusChangedRemoteEvent event) {
						switch (event.getConnection()) {
						case daq:
							daqOk = event.getStatus();
							break;
						default:
							return;
						}

						showGlass(daqOk.not().isTrue());
					}
				});
		
		EventChangedEvent.register(eventBus, new EventChangedEvent.Handler() {
			
			@Override
			public void onEventChanged(EventChangedEvent event) {
				String eventType = event.getEvent().getEventType();
				if (eventType.equals("PanicEvent")) {
					alarmMap.get(ptuId).setPanic(true);
				} else if (eventType.equals("DoseRateAlert")) {
					alarmMap.get(ptuId).setDose(true);
				} else if (eventType.equals("FallDetection")) {
					alarmMap.get(ptuId).setFall(true);
				}
				
				scheduler.update();
			}
		});

		SelectPtuEvent.subscribe(cmdBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				ptuId = event.getPtuId();
				scheduler.update();
			}
		});
		

		return true;
	}

	@Override
	public boolean update() {
		table.redraw();

		return false;
	}

}
