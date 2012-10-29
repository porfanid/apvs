package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.InterventionMapChangedEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.InterventionMap;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.widget.DynamicSelectionCell;
import ch.cern.atlas.apvs.client.widget.StringList;
import ch.cern.atlas.apvs.client.widget.TextInputSizeCell;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterPtuChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Shows a list of PTU settings which are alive. A list of ever alive PTU
 * settings is persisted.
 * 
 * @author duns
 * 
 */
public class PtuSettingsView extends VerticalFlowPanel implements Module {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();
	private ListHandler<String> columnSortHandler;

	protected PtuSettings settings = new PtuSettings();
	protected InterventionMap interventions = new InterventionMap();
	protected List<Integer> dosimeterSerialNumbers = new ArrayList<Integer>();

	private UpdateScheduler scheduler = new UpdateScheduler(this);

	public PtuSettingsView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {

		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();

		add(table);

		// ENABLED
		Column<String, Boolean> enabled = new Column<String, Boolean>(
				new CheckboxCell()) {
			@Override
			public Boolean getValue(String object) {
				return settings.isEnabled(object);
			}
		};
		enabled.setFieldUpdater(new FieldUpdater<String, Boolean>() {

			@Override
			public void update(int index, String object, Boolean value) {
				settings.setEnabled(object, value);
				fireSettingsChangedEvent(eventBus, settings);
			}
		});
		enabled.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		enabled.setSortable(true);
		table.addColumn(enabled, "Enabled");

		// PTU ID
		Column<String, String> ptuId = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return object;
			}
		};
		ptuId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		ptuId.setSortable(true);
		table.addColumn(ptuId, "PTU ID");

		// NAME
		Column<String, String> name = new Column<String, String>(new TextCell()) {
			@Override
			public String getValue(String object) {
				return interventions.get(object) != null ? interventions.get(
						object).getName() : "";
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		table.addColumn(name, "Name");

		// DOSIMETER
		Column<String, String> dosimeter = new Column<String, String>(
				new DynamicSelectionCell(new StringList<Integer>(
						dosimeterSerialNumbers))) {

			@Override
			public String getValue(String object) {
				return settings.getDosimeterSerialNumber(object).toString();
			}
		};
		dosimeter.setFieldUpdater(new FieldUpdater<String, String>() {

			@Override
			public void update(int index, String object, String value) {
				settings.setDosimeterSerialNumber(object,
						Integer.parseInt(value));
				fireSettingsChangedEvent(eventBus, settings);
			}
		});
		dosimeter.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dosimeter.setSortable(true);
		table.addColumn(dosimeter, "Dosimeter #");

		// HELMET URL
		Column<String, String> helmetUrl = new Column<String, String>(
				new TextInputSizeCell(50)) {
			@Override
			public String getValue(String object) {
				return settings.getCameraUrl(object, CameraView.HELMET);
			}
		};
		helmetUrl.setFieldUpdater(new FieldUpdater<String, String>() {

			@Override
			public void update(int index, String object, String value) {
				settings.setCameraUrl(object, CameraView.HELMET, value);
				fireSettingsChangedEvent(eventBus, settings);
			}
		});
		helmetUrl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		helmetUrl.setSortable(true);
		table.addColumn(helmetUrl, "Helmet Camera URL");

		// HAND URL
		Column<String, String> handUrl = new Column<String, String>(
				new TextInputSizeCell(50)) {
			@Override
			public String getValue(String object) {
				return settings.getCameraUrl(object, CameraView.HAND);
			}
		};
		handUrl.setFieldUpdater(new FieldUpdater<String, String>() {

			@Override
			public void update(int index, String object, String value) {
				settings.setCameraUrl(object, CameraView.HAND, value);
				fireSettingsChangedEvent(eventBus, settings);
			}
		});
		handUrl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		handUrl.setSortable(true);
		table.addColumn(handUrl, "Hand Camera URL");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<String>());

		// SORTING
		columnSortHandler = new ListHandler<String>(dataProvider.getList());
		columnSortHandler.setComparator(ptuId, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1 != null ? o1.compareTo(o2) : -1;
			}
		});
		columnSortHandler.setComparator(enabled, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return settings.isEnabled(o1).compareTo(settings.isEnabled(o2));
			}
		});
		columnSortHandler.setComparator(name, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return interventions.get(o1).getName()
						.compareTo(interventions.get(o2).getName());
			}
		});
		columnSortHandler.setComparator(dosimeter, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return settings.getDosimeterSerialNumber(o1).compareTo(
						settings.getDosimeterSerialNumber(o2));
			}
		});
		columnSortHandler.setComparator(helmetUrl, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return settings.getCameraUrl(o1, CameraView.HELMET).compareTo(
						settings.getCameraUrl(o2, CameraView.HELMET));
			}
		});
		columnSortHandler.setComparator(handUrl, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return settings.getCameraUrl(o1, CameraView.HAND).compareTo(
						settings.getCameraUrl(o2, CameraView.HAND));
			}
		});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(ptuId);

		PtuSettingsChangedEvent.subscribe(eventBus,
				new PtuSettingsChangedEvent.Handler() {
					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedEvent event) {
						log.info("PTU Settings changed");
						settings = event.getPtuSettings();
						dataProvider.getList().clear();
						dataProvider.getList().addAll(settings.getPtuIds());

						scheduler.update();
					}
				});

		InterventionMapChangedEvent.subscribe(eventBus,
				new InterventionMapChangedEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedEvent event) {
						interventions = event.getInterventionMap();
						scheduler.update();
					}
				});

		DosimeterSerialNumbersChangedEvent.subscribe(eventBus,
				new DosimeterSerialNumbersChangedEvent.Handler() {

					@Override
					public void onDosimeterSerialNumbersChanged(
							DosimeterSerialNumbersChangedEvent event) {
						dosimeterSerialNumbers.clear();
						dosimeterSerialNumbers.addAll(event
								.getDosimeterSerialNumbers());
						log.info("DOSI changed "
								+ dosimeterSerialNumbers.size());

						// FIXME, allow for setting not available as DOSI #
						scheduler.update();
					}
				});

		scheduler.update();

		return true;
	}

	@Override
	public boolean update() {
		// Resort the table
		ColumnSortEvent.fire(table, table.getColumnSortList());

		table.redraw();
		
		return false;
	}

	private void fireSettingsChangedEvent(EventBus eventBus,
			PtuSettings settings) {

		((RemoteEventBus) eventBus).fireEvent(new PtuSettingsChangedEvent(
				settings));
		((RemoteEventBus) eventBus).fireEvent(new DosimeterPtuChangedEvent(
				settings.getDosimeterToPtuMap()));
	}
}
