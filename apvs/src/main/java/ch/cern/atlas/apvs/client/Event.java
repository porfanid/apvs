package ch.cern.atlas.apvs.client;

import java.io.Serializable;

/**
 * @author Mark Donszelmann
 */
public class Event implements Serializable {

	private static final long serialVersionUID = 4209395741716413125L;
	private long code;
    private String data;

    public Event() {

    }

    public Event(long code, String data) {
        this.code = code;
        this.data = data;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return getCode() + ": " + getData();
    }


}
