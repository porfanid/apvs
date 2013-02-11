package ch.cern.atlas.apvs.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PasswordDialog extends DialogBox {

	private PasswordTextBox pwdBox;
	private List<DialogResultHandler> handlers = new ArrayList<DialogResultHandler>();

	public PasswordDialog() {
		setText("APVS");

		VerticalPanel panel = new VerticalPanel();
		setWidget(panel);

		panel.add(new Label("APVS Supervisor Password"));

		pwdBox = new PasswordTextBox();
		pwdBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					ok();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					cancel();
				}
			}
		});
		panel.add(pwdBox);

		HorizontalPanel bar = new HorizontalPanel();
		panel.add(bar);

		Button cancel = new Button("Cancel");
		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cancel();
			}
		});
		bar.add(cancel);

		Button ok = new Button("Ok");
		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ok();
			}
		});
		bar.add(ok);
	}

	public void addDialogResultHandler(DialogResultHandler handler) {
		handlers.add(handler);
	}

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				pwdBox.setFocus(true);
			}
		});
	}

	private void cancel() {
		hide();
		fireEvent(new DialogResultEvent(null));
	}

	private void ok() {
		hide();
		fireEvent(new DialogResultEvent(pwdBox.getText()));
	}

	private void fireEvent(DialogResultEvent event) {
		for (DialogResultHandler handler : handlers) {
			handler.onDialogResult(event);
		}
	}
}
