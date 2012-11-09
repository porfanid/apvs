package ch.cern.atlas.apvs.domain;

public class SensorOrder extends Order {

	private static final long serialVersionUID = 4892013083828443225L;

	private String name;

	public SensorOrder() {
	}

	public SensorOrder(String ptuId, String name, String parameter, String value) {
		super(ptuId, parameter, value);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return super.toString()+" "+getName();
	}

}
