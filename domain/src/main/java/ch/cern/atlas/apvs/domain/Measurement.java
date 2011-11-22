package ch.cern.atlas.apvs.domain;

import java.util.Date;

public class Measurement implements Comparable<Measurement> {

	private int ptuId;
	private String name;
	private double value;
	private String unit;
	private Date date;

	public Measurement() {
		this("---", 0, "");
	}

	public Measurement(String name, double value, String unit) {
		this(0, name, value, unit, new Date());
	}

	public Measurement(int ptuId, String name, double value, String unit, Date date) {
		this.ptuId = ptuId;
		this.name = name;
		this.value = value;
		this.unit = unit;
		this.date = date;
	}

	@Override
	public int compareTo(Measurement o) {
		// FIXME include PTU
		return (o != null) ? getName().compareTo(o.getName()) : 1;
	}
	
	public int getPtuId() {
		return ptuId;
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}
	
	public Date getDate() {
		return date;
	}

	public String toString() {
		return "Measurement("+getPtuId()+"): name="+getName()+" value="+getValue()+" unit="+getUnit()+" date: "+getDate();
	}
	
}
