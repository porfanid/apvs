package ch.cern.atlas.apvs.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.ModalHeader;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.constants.InputType;

import ch.cern.atlas.apvs.client.validation.InputField;
import ch.cern.atlas.apvs.client.validation.ValidationFieldset;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

public class PasswordDialog extends Modal {

	private InputField field;
	private List<DialogResultHandler> handlers = new ArrayList<DialogResultHandler>();

	public PasswordDialog() {		
		ValidationFieldset fieldset = new ValidationFieldset();
		field = new InputField("Supervisor Password");
		fieldset.add(field);
		
		Form form = new Form();
		form.setType(FormType.HORIZONTAL);
		form.add(fieldset);
		
		ModalHeader header = new ModalHeader();
		header.setTitle("Supervisor Password");

		ModalBody body = new ModalBody();
		body.add(form);

		Input input = field.getField();
		input.setType(InputType.PASSWORD);
		input.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					ok();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					cancel();
				}
			}
		});

		ModalFooter footer = new ModalFooter();

		Button cancel = new Button("Cancel");
		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cancel();
			}
		});
		footer.add(cancel);

		Button ok = new Button("Ok");
		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ok();
			}
		});
		footer.add(ok);
		
		// for close button
		addHiddenHandler(new ModalHiddenHandler() {
			@Override
			public void onHidden(ModalHiddenEvent evt) {
				fireEvent(new DialogResultEvent(null));
			}
		});
		
		add(header);
		add(body);
		add(footer);
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
//				field.setFocus(true);
			}
		});
	}

	private void cancel() {
		hide();
		fireEvent(new DialogResultEvent(null));
	}

	private void ok() {
		hide();
		fireEvent(new DialogResultEvent(field.getValue()));
	}

	private void fireEvent(DialogResultEvent event) {
		for (DialogResultHandler handler : handlers) {
			handler.onDialogResult(event);
		}
	}
}
