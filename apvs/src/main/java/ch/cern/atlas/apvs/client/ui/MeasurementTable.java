package ch.cern.atlas.apvs.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.ptu.shared.PtuClientConstants;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;

public class MeasurementTable extends AbstractMeasurementView {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private FlexTable table = new FlexTable();
	private Map<String, Integer> rows = new HashMap<String, Integer>();

	public MeasurementTable() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {

		super.configure(element, clientFactory, args);

		add(table, CENTER);

		if (showHeader) {
			rows.put("-Header-", 0);
		}

		// ClickableTextColumn<String> gauge = new ClickableTextColumn<String>()
		// {
		// @Override
		// public String getValue(String name) {
		// if ((name == null) || (historyMap == null) || (ptuId == null)) {
		// return "";
		// }
		// Measurement m = historyMap.getMeasurement(ptuId, name);
		// return m != null ? m.getLowLimit()+" "+m.getHighLimit() : "";
		// }
		//
		// @Override
		// public void render(Context context, String name, SafeHtmlBuilder sb)
		// {
		// Measurement m = historyMap != null ? historyMap.getMeasurement(ptuId,
		// name) : null;
		// if (m == null) {
		// return;
		// }
		// gaugeWidget.setValue(m.getValue(), m.getLowLimit(),
		// m.getHighLimit());
		// sb.appendEscaped(gaugeWidget.getElement().getInnerHTML());
		// Window.alert(gaugeWidget.getElement().getInnerHTML());
		// }
		//
		// };
		// gauge.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		// if (selectable) {
		// gauge.setFieldUpdater(new FieldUpdater<String, String>() {
		//
		// @Override
		// public void update(int index, String object, String value) {
		// selectMeasurement(object);
		// }
		// });
		// }
		// table.addColumn(gauge, showHeader ? new TextHeader("Limits")
		// : (Header<?>) null);

		return true;
	}

	@Override
	public boolean update() {
		boolean result = super.update();

		Window.alert("Update "+ptuId+" "+historyMap+" "+last);
		
		if ((historyMap != null) && (ptuId != null)) {

			if (last == null) {
				// full table redraw
				for(Measurement measurement : historyMap.getMeasurements(ptuId)) {
					redraw(measurement);
				}
			} else {
				// redraw last
				redraw(last);
			}
		}
		return result;
	}

	private void redraw(final Measurement measurement) {
		Integer row = rows.get(measurement.getName());
		if (row == null) {
			row = rows.size();
			rows.put(last.getName(), row);
		}

		int col = 0;

		// name
		HTML name = new HTML();
		if (selectable) {
			name.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					selectMeasurement(measurement.getName());
				}
			});
		}
		name.setHTML(decorate(measurement.getDisplayName(), measurement));
		// name.HorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		table.setWidget(row, col++, name);

		// value
		HTML value = new HTML();
		if (selectable) {
			value.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					selectMeasurement(measurement.getName());
				}
			});
		}

		value.setHTML(decorate(format.format(measurement.getValue()),
				measurement, last));
		// value.HorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.setWidget(row, col++, value);

		// unit
		HTML unit = new HTML();
		if (selectable) {
			unit.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					selectMeasurement(measurement.getName());
				}
			});
		}
		unit.setHTML(decorate(measurement.getUnit(), measurement));
		// unit.HorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		table.setWidget(row, col++, unit);

		if (showDate) {
			// unit
			HTML date = new HTML();
			if (selectable) {
				date.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						selectMeasurement(measurement.getName());
					}
				});
			}
			date.setHTML(decorate(
					PtuClientConstants.dateFormat.format(measurement.getDate()),
					measurement));
			// unit.HorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			table.setWidget(row, col++, date);

		}

		if (showHeader) {
			col = 0;
			if (showName) {
				// name
				String s = ptuId;
				if (s != null) {
					s = "PTU Id: " + ptuId;

					if (interventions != null) {
						String realName = interventions.get(ptuId) != null ? interventions
								.get(ptuId).getName() : null;

						if (realName != null) {
							s = "<div title=\"" + s + "\">" + realName
									+ "</div>";
						}
					}

					table.setHTML(0, col++, SafeHtmlUtils.fromSafeConstant(s));
				} else {
					table.setText(0, col++, "");
				}
			}

			table.setText(0, col++, "Value");
			table.setText(0, col++, "Unit");
			if (showDate) {
				table.setText(0, col++, "Date");
			}
		}

	}
}
