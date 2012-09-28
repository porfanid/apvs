package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

import ch.cern.atlas.apvs.util.StringUtils;

public class Measurement implements Message, Serializable,
		Comparable<Measurement> {

	private static final long serialVersionUID = -906069262585850986L;

	private String ptuId;
	private String name;
	private String displayName;
	private Number value;
	private String unit;
	private Date date;
	private Integer samplingRate;

	public Measurement() {
	}

	public Measurement(String name, Number value, String unit) {
		this(null, name, value, 10000, unit, new Date());
	}

	public Measurement(String ptuId, String name, Number value, String unit,
			Date date) {
		this(ptuId, name, value, 10000, unit, date);
	}

	public Measurement(String ptuId, String name, Number value,
			Integer samplingRate, String unit, Date date) {
		this.ptuId = ptuId;
		this.name = name;
		this.value = value;
		this.samplingRate = samplingRate;
		this.unit = unit;
		this.date = date;

		if (name.equals("O2")) {
			displayName = "O<sub>2</sub>";
		} else if (name.equals("CO2")) {
			displayName = "CO<sub>2</sub>";
		} else {
			displayName = StringUtils.join(
					StringUtils.splitByCharacterTypeCamelCase(name), ' ');
		}
	}

	public Measurement(String ptuId, String name, String displayName,
			Number value, Integer samplingRate, String unit, Date date) {
		this(ptuId, displayName, value, samplingRate, unit, date);
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
		return displayName;
	}

	public Number getValue() {
		return value;
	}

	public Integer getSamplingRate() {
		return samplingRate;
	}

	public String getUnit() {
		return unit;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String getType() {
		return "Measurement";
	}

	@Override
	public int compareTo(Measurement o) {
		// FIXME include PTU
		return (o != null) ? getName().compareTo(o.getName()) : 1;
	}

	@Override
	public int hashCode() {
		return (getPtuId() != null ? getPtuId().hashCode() : 0)
				+ (getName() != null ? getName().hashCode() : 0)
				+ (getValue() != null ? getValue().hashCode() : 0)
				+ (getSamplingRate() != null ? getSamplingRate().hashCode() : 0)
				+ (getUnit() != null ? getUnit().hashCode() : 0)
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
					&& (getSamplingRate() == null ? m.getSamplingRate() == null
							: getSamplingRate().equals(m.getSamplingRate()))
					&& (getUnit() == null ? m.getUnit() == null : getUnit()
							.equals(m.getUnit()))
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
				+ getValue() + " sampling rate=" + getSamplingRate() + " unit="
				+ getUnit() + " date: " + getDate();
	}

}
