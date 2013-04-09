package ch.cern.atlas.apvs.player;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class APVSPlayer implements EntryPoint {

    static final Logger logger = Logger.getLogger(APVSPlayer.class.getName());

    int count = 0;
    
    Button startButton;
    Button stopButton;
    
    @Override
    public void onModuleLoad() {
        
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                logger.log(Level.SEVERE, "Uncaught exception", e);
            }
        });

        Button button = new Button("Broadcast");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            }
        });

        Button post = new Button("Post");
        post.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            }
        });

        Button pollButton = new Button("Poll");

        RootPanel.get().add(button);
    
    }
    
}
