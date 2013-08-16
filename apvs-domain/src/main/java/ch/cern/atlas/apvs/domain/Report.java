package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Report implements Message, Serializable, IsSerializable {

	private static final long serialVersionUID = -2218198847285152101L;

	private volatile Device device;
	private String type = "Report";
	private Number batteryLevel;
	private boolean cameraHandheld;
	private boolean cameraHelmet;
	private boolean audio;
	private Date date;

	protected Report() {
	}
	
	public Report(Device device, double batteryLevel, boolean cameraHandheld, boolean cameraHelmet, boolean audio, Date date) {
		this.device = device;
		this.batteryLevel = batteryLevel;
		this.cameraHandheld = cameraHandheld;
		this.cameraHelmet = cameraHelmet;
		this.audio = audio;
		this.date = date;
	}

    @Override
	public Device getDevice() {
		return device;
	}

	public Number getBatteryLevel() {
		return batteryLevel;
	}

	public boolean getCameraHandheld() {
		return cameraHandheld;
	}

	public boolean getCameraHelmet() {
		return cameraHelmet;
	}

	public boolean getAudio() {
		return audio;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String getType() {
		return type;
	}
}
