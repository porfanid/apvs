package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import ch.cern.atlas.apvs.util.StringUtils;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
@Entity
@Table( name = "TBL_MEASUREMENTS" )
public class Measurement implements Message, Serializable, IsSerializable, 
		Comparable<Measurement> {

	private static final long serialVersionUID = -906069262585850986L;

    private Long id;
// device id
	private Date date;
	private Double value;
	private String unit;
	private String method;
	private Integer samplingRate;
	private String name;
	private Double highLimit;
	private Double lowLimit;
	
// FIXME to be added to the DB
	private boolean connected = true;

	private volatile String ptuId;
	private volatile transient String displayName;
	private transient String type = "Measurement";


	public Measurement() {
	}

	public Measurement(String ptuId, String name, Double value,
			Double lowLimit, Double highLimit, String unit,
			Integer samplingRate, Date date) {
		setPtuId(ptuId);
		setName(name);
		setValue(value);
		setLowLimit(lowLimit);
		setHighLimit(highLimit);
		setUnit(unit);
		setSamplingRate(samplingRate);
		setDate(date);

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
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name = "ID", length=15)
	public Long getId() {
		return id;
	}
	
	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getPtuId() {
		return ptuId;
	}
	
	private void setPtuId(String ptuId) {
		this.ptuId = ptuId;
	}

	@Column(name = "SENSOR", length=50)
	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getDisplayName() {
		return displayName != null ? displayName : getDisplayName(name);
	}

	@Column(name = "VALUE", length=1024)
	@Type(type="double_string")
	public Double getValue() {
		return value;
	}
	
	private void setValue(Double value) {
		this.value = value;
	}

	@Column(name = "DOWN_THRES", length=20)
	@Type(type="double_string")
	public Double getLowLimit() {
		return lowLimit;
	}
	
	private void setLowLimit(Double lowLimit) {
		this.lowLimit = lowLimit;
	}

	@Column(name = "UP_THRES", length=20)
	@Type(type="double_string")
	public Double getHighLimit() {
		return highLimit;
	}

	private void setHighLimit(Double highLimit) {
		this.highLimit = highLimit;
	}

	@Column(name = "UNIT", length=20)
	public String getUnit() {
		return unit;
	}
	
	private void setUnit(String unit) {
		this.unit = unit;
	}
	
	@Column(name = "METHOD", length=20)
	public String getMethod() {
		return method;
	}
	
	@SuppressWarnings("unused")
	private void setMethod(String method) {
		this.method = method;
	}

	@Column(name = "SAMPLING_RATE", length=20)
	@Type(type="integer_string")
	public Integer getSamplingRate() {
		return samplingRate;
	}
	
	private void setSamplingRate(Integer samplingRate) {
		this.samplingRate = samplingRate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATETIME")
	public Date getDate() {
		return date;
	}

	// to update the measurement in case of no changes
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	@Transient
	public String getType() {
		return type;
	}
	
	
	@Type(type="yes_no")
	@Column(name = "CONNECTED", length=1)
	public boolean isConnected() {
		return connected;
	}
	
	private void setConnected(boolean connected) {
		this.connected = connected;
	}

	public void disconnect() {
		setConnected(false);
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
		return "Measurement(" + getPtuId() + "): name:" + getName() + ", value:"
				+ getValue() + ", unit:" + getUnit() + ", sampling rate:"
				+ getSamplingRate() + ", date:" + getDate();
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
