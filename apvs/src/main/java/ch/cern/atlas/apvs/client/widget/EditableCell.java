package ch.cern.atlas.apvs.client.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class EditableCell extends AbstractCell<Object> {
	private TextInputSizeCell textInputCell;
	private MyEditTextCell editCell;
	private MySelectionCell selectionCell;
	private MyCheckboxCell checkboxCell;
	private MyButtonCell buttonCell;
	private MyTextCell textCell;

	private Class<? extends Cell<Object>>[] cellClass;

	public EditableCell(Class<? extends Cell<Object>>[] cellClass, int size) {
		this.cellClass = cellClass;

		textInputCell = new TextInputSizeCell(size);
		editCell = new MyEditTextCell();
		selectionCell = new MySelectionCell();
		checkboxCell = new MyCheckboxCell();
		buttonCell = new MyButtonCell();
		textCell = new MyTextCell();
	}

	public void setOptions(List<String> options) {
		selectionCell.setOptions(options);
	}

	@Override
	public boolean isEditing(Context context, Element parent, Object value) {
		Class<? extends Cell<? extends Object>> cellClass = getCellClass(context
				.getIndex());
		if (cellClass.equals(TextInputCell.class)) {
			return textInputCell.isEditing(context, parent, (String) value);
		} else if (cellClass.equals(EditTextCell.class)) {
			return editCell.isEditing(context, parent, (String) value);
		} else if (cellClass.equals(SelectionCell.class)) {
			return selectionCell.isEditing(context, parent, (String) value);
		} else if (cellClass.equals(CheckboxCell.class)) {
			return checkboxCell.isEditing(context, parent,
					Boolean.valueOf((String) value));
		} else if (cellClass.equals(ButtonCell.class)) {
			return buttonCell.isEditing(context, parent, (String) value);
		} else {
			return textCell.isEditing(context, parent, (String) value);
		}
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Object value,
			NativeEvent event, final ValueUpdater<Object> valueUpdater) {
		Class<? extends Cell<? extends Object>> cellClass = getCellClass(context
				.getIndex());

		if (cellClass.equals(TextInputCell.class)) {
			textInputCell.onBrowserEvent(context, parent, (String) value,
					event, new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else if (cellClass.equals(EditTextCell.class)) {
			editCell.onBrowserEvent(context, parent, (String) value, event,
					new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else if (cellClass.equals(SelectionCell.class)) {
			selectionCell.onBrowserEvent(context, parent, (String) value,
					event, new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else if (cellClass.equals(CheckboxCell.class)) {
			checkboxCell.onBrowserEvent(context, parent,
					Boolean.valueOf((String) value), event,
					new ValueUpdater<Boolean>() {
						@Override
						public void update(Boolean value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else if (cellClass.equals(ButtonCell.class)) {
			buttonCell.onBrowserEvent(context, parent, (String) value, event,
					new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else {
			textCell.onBrowserEvent(context, parent, (String) value, event,
					new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		}
	}

	@Override
	public void render(Context context, Object value, SafeHtmlBuilder sb) {
		Class<? extends Cell<? extends Object>> cellClass = getCellClass(context
				.getIndex());
		if (cellClass.equals(TextInputCell.class)) {
			textInputCell.render(context, (String) value, sb);
		} else if (cellClass.equals(EditTextCell.class)) {
			editCell.render(context, (String) value, sb);
		} else if (cellClass.equals(SelectionCell.class)) {
			selectionCell.render(context, (String) value, sb);
		} else if (cellClass.equals(CheckboxCell.class)) {
			checkboxCell.render(context, Boolean.valueOf((String) value), sb);
		} else if (cellClass.equals(ButtonCell.class)) {
			if (value instanceof SafeHtml) {
				buttonCell.render(context, (SafeHtml) value, sb);
			} else {
				buttonCell.render(context, (String) value, sb);
			}
		} else {
			if (value instanceof SafeHtml) {
				textCell.render(context, (SafeHtml) value, sb);
			} else {
				textCell.render(context, (String) value, sb);
			}
		}
	}

	@Override
	public boolean dependsOnSelection() {
		return false;
	}

	@Override
	public Set<String> getConsumedEvents() {
		Set<String> events = new HashSet<String>();

		Set<String> textInputCellEvents = textInputCell.getConsumedEvents();
		if (textInputCellEvents != null) {
			events.addAll(textInputCellEvents);
		}
		Set<String> editCellEvents = editCell.getConsumedEvents();
		if (editCellEvents != null) {
			events.addAll(editCellEvents);
		}
		Set<String> selectionCellEvents = selectionCell.getConsumedEvents();
		if (selectionCellEvents != null) {
			events.addAll(selectionCellEvents);
		}
		Set<String> checkboxCellEvents = checkboxCell.getConsumedEvents();
		if (checkboxCellEvents != null) {
			events.addAll(checkboxCellEvents);
		}
		Set<String> buttonCellEvents = buttonCell.getConsumedEvents();
		if (buttonCellEvents != null) {
			events.addAll(buttonCellEvents);
		}
		Set<String> textCellEvents = textCell.getConsumedEvents();
		if (textCellEvents != null) {
			events.addAll(textCellEvents);
		}
		return events;
	}

	@Override
	public boolean handlesSelection() {
		return false;
	}

	@Override
	protected void onEnterKeyDown(Context context, Element parent,
			Object value, NativeEvent event,
			final ValueUpdater<Object> valueUpdater) {
		Class<? extends Cell<? extends Object>> cellClass = getCellClass(context
				.getIndex());
		if (cellClass.equals(TextInputCell.class)) {
			textInputCell.onEnterKeyDown(context, parent, (String) value,
					event, new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else if (cellClass.equals(EditTextCell.class)) {
			editCell.onEnterKeyDown(context, parent, (String) value, event,
					new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else if (cellClass.equals(SelectionCell.class)) {
			selectionCell.onEnterKeyDown(context, parent, (String) value,
					event, new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else if (cellClass.equals(CheckboxCell.class)) {
			checkboxCell.onEnterKeyDown(context, parent,
					Boolean.valueOf((String) value), event,
					new ValueUpdater<Boolean>() {
						@Override
						public void update(Boolean value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else if (cellClass.equals(ButtonCell.class)) {
			buttonCell.onEnterKeyDown(context, parent, (String) value, event,
					new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		} else {
			textCell.onEnterKeyDown(context, parent, (String) value, event,
					new ValueUpdater<String>() {
						@Override
						public void update(String value) {
							if (valueUpdater != null) {
								valueUpdater.update(value);
							}
						}
					});
		}
	}

	@Override
	public boolean resetFocus(Context context, Element parent, Object value) {
		Class<? extends Cell<? extends Object>> cellClass = getCellClass(context
				.getIndex());
		if (cellClass.equals(TextInputCell.class)) {
			return textInputCell.resetFocus(context, parent, (String) value);
		} else if (cellClass.equals(EditTextCell.class)) {
			return editCell.resetFocus(context, parent, (String) value);
		} else if (cellClass.equals(SelectionCell.class)) {
			return selectionCell.resetFocus(context, parent, (String) value);
		} else if (cellClass.equals(CheckboxCell.class)) {
			return checkboxCell.resetFocus(context, parent,
					Boolean.valueOf((String) value));
		} else if (cellClass.equals(ButtonCell.class)) {
			return buttonCell.resetFocus(context, parent, (String) value);
		} else {
			return textCell.resetFocus(context, parent, (String) value);
		}
	}

	@Override
	public void setValue(Context context, Element parent, Object value) {
		Class<? extends Cell<? extends Object>> cellClass = getCellClass(context
				.getIndex());
		if (cellClass.equals(TextInputCell.class)) {
			textInputCell.setValue(context, parent, (String) value);
		} else if (cellClass.equals(EditTextCell.class)) {
			editCell.setValue(context, parent, (String) value);
		} else if (cellClass.equals(SelectionCell.class)) {
			selectionCell.setValue(context, parent, (String) value);
		} else if (cellClass.equals(CheckboxCell.class)) {
			checkboxCell.setValue(context, parent,
					Boolean.valueOf((String) value));
		} else if (cellClass.equals(ButtonCell.class)) {
			buttonCell.setValue(context, parent, (String) value);
		} else {
			textCell.setValue(context, parent, (String) value);
		}
	}

	private Class<? extends Cell<? extends Object>> getCellClass(int row) {
		if ((0 <= row) && (row < cellClass.length)) {
			return cellClass[row];
		}
		return TextCell.class;
	}

	private class MyEditTextCell extends EditTextCell {
		@Override
		protected void onEnterKeyDown(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, String value, NativeEvent event,
				ValueUpdater<String> valueUpdater) {
			super.onEnterKeyDown(context, parent, value, event, valueUpdater);
		}

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				String value, SafeHtmlBuilder sb) {
			if ((value != null) && (value.length() > 20)) {
				value = value.substring(0, 10) + "..."
						+ value.substring(value.length() - 10);
			}
			super.render(context, value, sb);
		}
	}

	private class MySelectionCell extends DynamicSelectionCell {

		@Override
		protected void onEnterKeyDown(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, String value, NativeEvent event,
				ValueUpdater<String> valueUpdater) {
			super.onEnterKeyDown(context, parent, value, event, valueUpdater);
		}
	}

	private class MyCheckboxCell extends CheckboxCell {
		@Override
		protected void onEnterKeyDown(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Boolean value, NativeEvent event,
				ValueUpdater<Boolean> valueUpdater) {
			super.onEnterKeyDown(context, parent, value, event, valueUpdater);
		}
	}

	private class MyButtonCell extends ButtonCell {
		@Override
		protected void onEnterKeyDown(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, String value, NativeEvent event,
				ValueUpdater<String> valueUpdater) {
			super.onEnterKeyDown(context, parent, value, event, valueUpdater);
		}
	}

	private class MyTextCell extends TextCell {
		@Override
		protected void onEnterKeyDown(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, String value, NativeEvent event,
				ValueUpdater<String> valueUpdater) {
			super.onEnterKeyDown(context, parent, value, event, valueUpdater);
		}
	}
}
