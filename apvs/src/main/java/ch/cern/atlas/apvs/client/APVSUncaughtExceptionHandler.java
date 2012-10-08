package ch.cern.atlas.apvs.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DialogBox;

public class APVSUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	@Override
	public void onUncaughtException(Throwable throwable) {
		String text = "Uncaught exception: ";
		while (throwable != null) {
			StackTraceElement[] stackTraceElements = throwable.getStackTrace();
			text += throwable.toString() + "\n";
			for (int i = 0; i < stackTraceElements.length; i++) {
				text += "    at " + stackTraceElements[i] + "\n";
			}
			throwable = throwable.getCause();
			if (throwable != null) {
				text += "Caused by: ";
			}
		}
		DialogBox dialogBox = new DialogBox(true, false);
		DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor",
				"#ABCDEF");
		log.warn(text);
		text = text.replaceAll(" ", "&nbsp;");
		dialogBox.setHTML("<pre>" + text + "</pre>");
		dialogBox.center();
	}

}
