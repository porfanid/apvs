package ch.cern.atlas.apvs.client;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Mark Donszelmann
 */
public class Info extends PopupPanel {

    public static void display(String title, String message) {

        final Info info = new Info(title, message);

        info.show();

        Timer t = new Timer() {
            @Override
            public void run() {
                info.hide();
            }
        };
        t.schedule(4000);
    }

    @Override
    public void show() {
        super.show();
        slots.add(level, this);
    }


    @Override
    public void hide() {
        super.hide();
        slots.set(level, null);
    }


    protected Info(String title, String message) {

        add(new InfoWidget(title, message));
        setWidth("300px");
        setHeight("50px");

        int root_width = Window.getClientWidth();
        int root_height = Window.getClientHeight();

        level = findAvailableLevel();

        int left = root_width - 320;
        int top = root_height - 80 - (level * 60);

        setPopupPosition(left, top);
    }

    private static ArrayList<Info> slots = new ArrayList<Info>();

    private int level;

    private static int findAvailableLevel() {
        int size = slots.size();
        for (int i = 0; i < size; i++) {
            if (slots.get(i) == null) {
                return i;
            }
        }
        return size;
    }
}
