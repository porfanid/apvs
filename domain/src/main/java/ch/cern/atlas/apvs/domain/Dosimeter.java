package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

public class Dosimeter implements Serializable {

	private static final long serialVersionUID = -9183933693411766044L;

	private int serialNo;
	private double dose;
	private double rate;
	
	public Dosimeter() {
	}

	public Dosimeter(int serialNo, double dose, double rate) {
		this.serialNo = serialNo;
		this.dose = dose;
		this.rate = rate;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public double getDose() {
		return dose;
	}

	public double getRate() {
		return rate;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(getSerialNo()).hashCode()
				+ Double.valueOf(getDose()).hashCode()
				+ Double.valueOf(getRate()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Dosimeter) {
			Dosimeter d = (Dosimeter) obj;
			return (getSerialNo() == d.getSerialNo())
					&& (getDose() == d.getDose()) && (getRate() == d.getRate());
		}
		return super.equals(obj);
	}

	public String toString() {
		return "Dosimeter (" + getSerialNo() + "): dose=" + getDose()
				+ "; rate=" + getRate();
	}
}
