package ch.cern.atlas.apvs.client.ui;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.domain.Intervention;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.service.DbServiceAsync;
import ch.cern.atlas.apvs.client.service.InterventionServiceAsync;
import ch.cern.atlas.apvs.client.widget.ClickableHtmlColumn;
import ch.cern.atlas.apvs.client.widget.DurationCell;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.ptu.shared.PtuClientConstants;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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
	private boolean databaseConnected;

	private boolean showHeader = true;

	private String options;

	private List<String> names = Arrays.asList(new String[] { "Audio Status",
			"Video Status", "Database Status", "Start Time", "Duration" });
	private List<Class<?>> classes = Arrays.asList(new Class<?>[] {
			CheckboxCell.class, CheckboxCell.class, CheckboxCell.class,
			DateCell.class, DurationCell.class });

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
				if (name.equals("Audio Status")) {
					// FIXME #191
					return false;
				} else if (name.equals("Video Status")) {
					// FIXME #192
					return false;
				} else if (name.equals("Database Status")) {
					return databaseConnected;
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

		if (cmdBus != null) {
			SelectPtuEvent.subscribe(cmdBus, new SelectPtuEvent.Handler() {

				@Override
				public void onPtuSelected(SelectPtuEvent event) {
					ptuId = event.getPtuId();
					updateIntervention();
					update();
				}
			});
		}

		Timer timer = new Timer() {
			@Override
			public void run() {
				updateDatabase();
				updateIntervention();
				update();
			}
		};
		timer.scheduleRepeating(60000);

		updateDatabase();
		updateIntervention();

		return true;
	}

	private void updateDatabase() {
		DbServiceAsync.Util.getInstance().isConnected(new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				databaseConnected = result;
				update();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				log.warn("Caught: "+caught);
			}
		});
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
						update();
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
