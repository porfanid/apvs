package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Alarm implements Serializable, IsSerializable {

	private static final long serialVersionUID = -5562745209109162633L;

	private String ptuId;
	private boolean panic, dose, fall;
	
	public Alarm() {
	}
	
	public Alarm(String ptuId) {
		this.ptuId = ptuId;
	}

	public String getPtuId() {
		return ptuId;
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
		return ptuId+" panic: "+panic+"; dose: "+dose+"; fall: "+fall;
	}

}
