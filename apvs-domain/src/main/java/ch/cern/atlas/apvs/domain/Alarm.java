package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

public class Alarm implements Serializable {

	private static final long serialVersionUID = -5562745209109162633L;

	private String ptuId;
	private boolean panic = true, dose, fall = true;
	
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



}
