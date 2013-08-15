package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Alarm implements Serializable, IsSerializable {

	private static final long serialVersionUID = -5562745209109162633L;

	private Device device;
	private boolean panic, dose, fall;
	
	public Alarm() {
	}
	
	public Alarm(Device device) {
		this.device = device;
	}

	public Device getPtu() {
		return device;
	}

	public boolean isPanic() {
		return panic;
	}

	public void setPanic(boolean panic) {
		this.panic = panic;
	}

	public boolean isFall() {
		return fall;
	}

	public void setFall(boolean fall) {
		this.fall = fall;
	}

	public boolean isDose() {
		return dose;
	}

	public void setDose(boolean dose) {
		this.dose = dose;
	}

	public String toString() {
		return device+" panic: "+panic+"; dose: "+dose+"; fall: "+fall;
	}

}
