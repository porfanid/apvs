package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Event implements Message, Serializable, IsSerializable {

	private static final long serialVersionUID = -5549380949230727273L;
	
	private String ptuId;
	private String name;
	private String eventType;
	private Number value;
	private Number threshold;
	private String unit;
	private Date date;

	public Event() {
	}

	public Event(String ptuId, String name, String eventType, Number value, Number threshold, String unit, Date date) {
		this.ptuId = ptuId;
		this.name = name;
		this.eventType = eventType;
		this.value = value;
		this.threshold = threshold;
		this.unit = unit;
		this.date = date;
	}

    @Override
	public String getPtuId() {
		return ptuId;
	}

	public String getName() {
		return name;
	}

	public String getEventType() {
		return eventType;
	}

	public Number getValue() {
		return value;
	}
	
	public Number getTheshold() {
		return threshold;
	}

	public String getUnit() {
		return unit;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String getType() {
		return "Event";
	}

	
	public String toString() {
		return getDate()+"PTU: "+getPtuId()+" Sensor: "+getName()+" Type: "+getEventType()+" Value: "+getValue()+" Unit: "+getUnit()+" Threshold: "+getTheshold();
	}
}
