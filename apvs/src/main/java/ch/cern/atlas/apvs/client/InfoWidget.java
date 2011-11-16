package ch.cern.atlas.apvs.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Mark Donszelmann
 */
public class InfoWidget extends Composite {
    MyUiBinder binder = GWT.create(MyUiBinder.class);

    interface MyUiBinder extends UiBinder<Widget, InfoWidget> {
    }

    @UiField
    Label title;
    @UiField
    Label message;

    public InfoWidget(String title, String message) {
        initWidget(binder.createAndBindUi(this));
        this.title.setText(title);
        this.message.setText(message);
    }
}
