package ch.cern.atlas.apvs.client.ui;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.domain.Intervention;
import ch.cern.atlas.apvs.client.event.ConnectionStatusChangedEvent;
import ch.cern.atlas.apvs.client.event.ConnectionStatusChangedEvent.ConnectionType;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.service.InterventionServiceAsync;
import ch.cern.atlas.apvs.client.widget.ClickableHtmlColumn;
import ch.cern.atlas.apvs.client.widget.DurationCell;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.ptu.shared.PtuClientConstants;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;

public class GeneralInfoView extends VerticalFlowPanel implements Module {

	private Logger log = LoggerFactory.getLogger(getClass());
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();

	private EventBus cmdBus;

	private String ptuId;
	private Date startTime;
	private boolean audioOk, videoOk, daqOk, databaseOk;

	private boolean showHeader = true;

	private String options;

	private List<String> names = Arrays.asList(new String[] {
			ConnectionType.audio.getString(), ConnectionType.video.getString(),
			ConnectionType.daq.getString(), ConnectionType.database.getString(),
			"Start Time", "Duration" });
	private List<Class<?>> classes = Arrays.asList(new Class<?>[] {
			CheckboxCell.class, CheckboxCell.class, CheckboxCell.class, CheckboxCell.class,
			DateCell.class, DurationCell.class });

	private UpdateScheduler scheduler = new UpdateScheduler(this);

	public GeneralInfoView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {

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
		cell.setEnabled(false);
		Column<String, Object> column = new Column<String, Object>(cell) {
			@Override
			public Object getValue(String name) {
				if (name.equals(ConnectionType.audio.getString())) {
					return audioOk;
				} else if (name.equals(ConnectionType.video.getString())) {
					return videoOk;
				} else if (name.equals(ConnectionType.daq.getString())) {
					return daqOk;
				} else if (name.equals(ConnectionType.database.getString())) {
					return databaseOk;
				} else if (name.equals("Start Time")) {
					return startTime;
				} else if (name.equals("Duration")) {
					return startTime != null ? new Date().getTime()
							- startTime.getTime() : null;
				}
				return null;
			}

			@Override
			public void render(Context context, String name, SafeHtmlBuilder sb) {
				Object s = getValue(name);
				getCell().render(context, s, sb);
			}
		};
		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.addColumn(column, showHeader ? new TextHeader("Value") : null);

		dataProvider.addDataDisplay(table);
		dataProvider.setList(names);

		ConnectionStatusChangedEvent.subscribe(
				clientFactory.getRemoteEventBus(),
				new ConnectionStatusChangedEvent.Handler() {

					@Override
					public void onConnectionStatusChanged(
							ConnectionStatusChangedEvent event) {
						switch (event.getConnection()) {
						case audio:
							// FIXME #191, not sent yet
							audioOk = event.isOk();
							break;
						case video:
							// FIXME #192, not sent yet
							videoOk = event.isOk();
							break;
						case daq:
							daqOk = event.isOk();
							break;
						case database:
							databaseOk = event.isOk();
							break;
						default:
							return;
						}
						scheduler.update();
					}
				});

		if (cmdBus != null) {
			SelectPtuEvent.subscribe(cmdBus, new SelectPtuEvent.Handler() {

				@Override
				public void onPtuSelected(SelectPtuEvent event) {
					ptuId = event.getPtuId();
					updateIntervention();
					scheduler.update();
				}
			});
		}

		// FIXME #258
		Timer timer = new Timer() {
			@Override
			public void run() {
				updateIntervention();
				scheduler.update();
			}
		};
		timer.scheduleRepeating(60000);

		updateIntervention();

		return true;
	}

	private void updateIntervention() {
		if (ptuId == null) {
			startTime = null;
			return;
		}

		InterventionServiceAsync.Util.getInstance().getIntervention(ptuId,
				new AsyncCallback<Intervention>() {

					@Override
					public void onSuccess(Intervention result) {
						startTime = result != null ? result.getStartTime()
								: null;
						scheduler.update();
					}

					@Override
					public void onFailure(Throwable caught) {
						log.warn("Caught : " + caught);
					}
				});
	}

	@Override
	public boolean update() {
		table.redraw();
		return false;
	}

}
