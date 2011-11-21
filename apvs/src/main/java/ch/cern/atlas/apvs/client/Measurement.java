package ch.cern.atlas.apvs.client;

public class Measurement implements Comparable<Measurement> {

	private String name;
	private double value;
	private String unit;

	public Measurement() {
		name = "---";
		value = 0;
		unit = "";
	}

	public Measurement(String name, double value, String unit) {
		this.name = name;
		this.value = value;
		this.unit = unit;
	}

	@Override
	public int compareTo(Measurement o) {
		return (o != null) ? getName().compareTo(o.getName()) : 1;
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

}
