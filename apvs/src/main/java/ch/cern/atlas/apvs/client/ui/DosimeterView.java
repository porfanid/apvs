package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.ConnectionStatusChangedEvent;
import ch.cern.atlas.apvs.client.event.InterventionMapChangedEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.InterventionMap;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.widget.GlassPanel;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

public class DosimeterView extends GlassPanel implements Module {

	private PtuSettings settings;
	private InterventionMap interventions;
	private ListDataProvider<Dosimeter> dataProvider = new ListDataProvider<Dosimeter>();
	private CellTable<Dosimeter> table = new CellTable<Dosimeter>();
	private ListHandler<Dosimeter> columnSortHandler;

	private UpdateScheduler scheduler = new UpdateScheduler(this);
	protected boolean daqOk;
	protected boolean databaseOk;

	public DosimeterView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {

		RemoteEventBus remoteEventBus = clientFactory.getRemoteEventBus();

		add(table, CENTER);

		TextColumn<Dosimeter> name = new TextColumn<Dosimeter>() {
			@Override
			public String getValue(Dosimeter object) {
				return getName(object);
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		table.addColumn(name, "Name");

		Column<Dosimeter, Number> serialNo = new Column<Dosimeter, Number>(
				new NumberCell(NumberFormat.getFormat("0"))) {
			@Override
			public Number getValue(Dosimeter object) {
				return object.getSerialNo();
			}
		};
		serialNo.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		serialNo.setSortable(true);
		table.addColumn(serialNo, "Serial No");

		Column<Dosimeter, Number> dose = new Column<Dosimeter, Number>(
				new NumberCell(NumberFormat.getFormat("0"))) {
			@Override
			public Number getValue(Dosimeter object) {
				return object.getDose();
			}
		};
		dose.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		dose.setSortable(true);
		table.addColumn(dose,
				SafeHtmlUtils.fromSafeConstant("Dose [&micro;Sv]"));

		Column<Dosimeter, Number> rate = new Column<Dosimeter, Number>(
				new NumberCell(NumberFormat.getFormat("0.0"))) {
			@Override
			public Number getValue(Dosimeter object) {
				return object.getRate();
			}
		};
		rate.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		rate.setSortable(true);
		table.addColumn(rate,
				SafeHtmlUtils.fromSafeConstant("Rate [&micro;Sv/h]"));

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<Dosimeter>());

		columnSortHandler = new ListHandler<Dosimeter>(dataProvider.getList());
		columnSortHandler.setComparator(name, new Comparator<Dosimeter>() {
			public int compare(Dosimeter o1, Dosimeter o2) {
				return getName(o1).compareTo(getName(o2));
			}
		});
		columnSortHandler.setComparator(serialNo, new Comparator<Dosimeter>() {
			public int compare(Dosimeter o1, Dosimeter o2) {
				return o1 != null ? o1.compareTo(o2) : -1;
			}
		});
		columnSortHandler.setComparator(dose, new Comparator<Dosimeter>() {
			public int compare(Dosimeter o1, Dosimeter o2) {
				if (o1 == o2) {
					return 0;
				}

				if (o1 != null) {
					return (o2 != null) ? o1.getDose() < o2.getDose() ? -1 : o1
							.getDose() == o2.getDose() ? 0 : 1 : 1;
				}
				return -1;
			}
		});
		columnSortHandler.setComparator(rate, new Comparator<Dosimeter>() {
			public int compare(Dosimeter o1, Dosimeter o2) {
				if (o1 == o2) {
					return 0;
				}

				if (o1 != null) {
					return (o2 != null) ? o1.getRate() < o2.getRate() ? -1 : o1
							.getRate() == o2.getRate() ? 0 : 1 : 1;
				}
				return -1;
			}
		});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(serialNo);

		ConnectionStatusChangedEvent.subscribe(
				clientFactory.getRemoteEventBus(),
				new ConnectionStatusChangedEvent.Handler() {

					@Override
					public void onConnectionStatusChanged(
							ConnectionStatusChangedEvent event) {
						switch (event.getConnection()) {
						case daq:
							daqOk = event.isOk();
							break;
						case database:
							databaseOk = event.isOk();
							break;
						default:
							break;
						}

						showGlass(!daqOk || !databaseOk);
					}
				});
		
		PtuSettingsChangedEvent.subscribe(remoteEventBus,
				new PtuSettingsChangedEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedEvent event) {
						settings = event.getPtuSettings();

						scheduler.update();
					}
				});

		InterventionMapChangedEvent.subscribe(remoteEventBus,
				new InterventionMapChangedEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedEvent event) {
						interventions = event.getInterventionMap();
						scheduler.update();
					}
				});

		DosimeterChangedEvent.subscribe(remoteEventBus,
				new DosimeterChangedEvent.Handler() {

					@Override
					public void onDosimeterChanged(DosimeterChangedEvent event) {
						Dosimeter dosimeter = event.getDosimeter();

						List<Dosimeter> list = dataProvider.getList();
						int i = 0;
						while (i < list.size()) {
							if (list.get(i).getSerialNo() == dosimeter
									.getSerialNo()) {
								list.set(i, dosimeter);
								break;
							}
							i++;
						}
						if (i == list.size()) {
							list.add(dosimeter);
						}

						scheduler.update();
					}
				});

		scheduler.update();

		return true;
	}

	private String getName(Dosimeter object) {
		if ((object == null) || (settings == null))
			return "";

		String ptuId = settings.getPtuId(object.getSerialNo());
		if (ptuId == null)
			return "";

		String name = interventions.get(ptuId) != null ? interventions.get(
				ptuId).getName() : null;
		return name != null ? name : "";
	}

	@Override
	public boolean update() {
		// Resort the table
		ColumnSortEvent.fire(table, table.getColumnSortList());
		table.redraw();
		
		return false;
	}
}
