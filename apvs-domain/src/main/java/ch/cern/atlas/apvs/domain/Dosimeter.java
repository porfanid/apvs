package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Dosimeter implements Serializable, IsSerializable, Comparable<Dosimeter> {

	private static final long serialVersionUID = -9183933693411766044L;

	private String serialNo;
	private double dose;
	private double rate;
	private Date date;

	public Dosimeter() {
	}

	public Dosimeter(String serialNo, double dose, double rate, Date date) {
		this.serialNo = serialNo;
		this.dose = dose;
		this.rate = rate;
		this.date = date;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public double getDose() {
		return dose;
	}

	public double getRate() {
		return rate;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public int hashCode() {
		return getSerialNo().hashCode() + Double.valueOf(getDose()).hashCode()
				+ Double.valueOf(getRate()).hashCode() + date.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Dosimeter) {
			Dosimeter d = (Dosimeter) obj;
			return getSerialNo().equals(d.getSerialNo())
					&& (getDose() == d.getDose()) && (getRate() == d.getRate())
					&& (getDate() == d.getDate());
		}
		return super.equals(obj);
	}

	public String toString() {
		return "Dosimeter '" + getSerialNo() + "': dose=" + getDose()
				+ "; rate=" + getRate() + "; date=" + getDate();
	}

	@Override
	public int compareTo(Dosimeter o) {
		if (this == o) {
			return 0;
		}

		return getSerialNo().compareTo(o.getSerialNo());
	}
}
