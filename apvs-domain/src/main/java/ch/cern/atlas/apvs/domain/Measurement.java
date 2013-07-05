package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

import ch.cern.atlas.apvs.util.StringUtils;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Measurement implements Message, Serializable, IsSerializable, 
		Comparable<Measurement> {

	private static final long serialVersionUID = -906069262585850986L;

	private volatile String ptuId;
	private volatile String displayName;
	private String type = "Measurement";
	private String name;
	private Double value;
	private Double lowLimit;
	private Double highLimit;
	private String unit;
	private Date date;
	private Integer samplingRate;

	public Measurement() {
	}

	public Measurement(String ptuId, String name, Double value,
			Double lowLimit, Double highLimit, String unit,
			Integer samplingRate, Date date) {
		this.ptuId = ptuId;
		this.name = name;
		this.value = value;
		this.lowLimit = lowLimit;
		this.highLimit = highLimit;
		this.unit = unit;
		this.samplingRate = samplingRate;
		this.date = date;

		this.displayName = null;

		// Fix Unit for Body Temperature and Temperature
		if ((name != null) && (name.equals("Temperature") || name.equals("BodyTemperature"))
				&& (unit != null) && unit.equals("C")) {
			this.unit = "&deg;C";
		}
	}

	public Measurement(String ptuId, String name, String displayName,
			Double value, Double lowLimit, Double highLimit, String unit,
			Integer samplingRate, Date date) {
		this(ptuId, displayName, value, lowLimit, highLimit, unit,
				samplingRate, date);
		this.displayName = displayName;
	}

	@Override
	public String getPtuId() {
		return ptuId;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName != null ? displayName : getDisplayName(name);
	}

	public Double getValue() {
		return value;
	}

	public Double getLowLimit() {
		return lowLimit;
	}

	public Double getHighLimit() {
		return highLimit;
	}

	public String getUnit() {
		return unit;
	}

	public Integer getSamplingRate() {
		return samplingRate;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public int compareTo(Measurement o) {
		// FIXME include ptuId
		return (o != null) ? getName().compareTo(o.getName()) : 1;
	}

	@Override
	public int hashCode() {
		return (getPtuId() != null ? getPtuId().hashCode() : 0)
				+ (getName() != null ? getName().hashCode() : 0)
				+ (getValue() != null ? getValue().hashCode() : 0)
				+ (getLowLimit() != null ? getLowLimit().hashCode() : 0)
				+ (getHighLimit() != null ? getHighLimit().hashCode() : 0)
				+ (getUnit() != null ? getUnit().hashCode() : 0)
				+ (getSamplingRate() != null ? getSamplingRate().hashCode() : 0)
				+ (getDate() != null ? getDate().hashCode() : 0)
				+ (getType() != null ? getType().hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof Measurement)) {
			Measurement m = (Measurement) obj;
			return (getPtuId() == null ? m.getPtuId() == null : getPtuId()
					.equals(m.getPtuId()))
					&& (getName() == null ? m.getName() == null : getName()
							.equals(m.getName()))
					&& (getValue() == null ? m.getValue() == null : getValue()
							.equals(m.getValue()))
					&& (getLowLimit() == null ? m.getLowLimit() == null
							: getLowLimit().equals(m.getLowLimit()))
					&& (getHighLimit() == null ? m.getHighLimit() == null
							: getHighLimit().equals(m.getHighLimit()))
					&& (getUnit() == null ? m.getUnit() == null : getUnit()
							.equals(m.getUnit()))
					&& (getSamplingRate() == null ? m.getSamplingRate() == null
							: getSamplingRate().equals(m.getSamplingRate()))
					&& (getDate() == null ? m.getDate() == null : getDate()
							.equals(m.getDate()))
					&& (getType() == null ? m.getType() == null : getType()
							.equals(m.getType()));
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return "Measurement(" + getPtuId() + "): name=" + getName() + " value="
				+ getValue() + " unit=" + getUnit() + " sampling rate="
				+ getSamplingRate() + " date: " + getDate();
	}

	public static String getDisplayName(String name) {
		// NOTE: <sub> not supported in titles:
		// http://sourceforge.net/p/gwt-highcharts/discussion/general/thread/4fbb5734/
		if (name.equals("O2")) {
			return "O\u2082";
		} else if (name.equals("CO2")) {
			return "CO\u2082";
		}
		return StringUtils.join(
				StringUtils.splitByCharacterTypeCamelCase(name), ' ');
	}

}
