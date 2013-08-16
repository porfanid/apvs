package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Type;

import ch.cern.atlas.apvs.util.StringUtils;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
@Entity
@Table( name = "TBL_MEASUREMENTS" )
public class Measurement implements Message, Serializable, IsSerializable, 
		Comparable<Measurement> {

	private static final long serialVersionUID = -906069262585850986L;

	private volatile Device device;
    private Long id;
	private Date date;
	private Double value;
	private String unit;
	private String method;
	private Integer samplingRate;
	private String sensor;
	private Double highLimit;
	private Double lowLimit;
	
// FIXME to be added to the DB
	private boolean connected = true;

	private volatile transient String displayName;
	private transient String type = "Measurement";

	protected Measurement() {
	}
	
	public Measurement(Device device, String sensor, Double value,
			Double lowLimit, Double highLimit, String unit,
			Integer samplingRate, Date date) {
		setDevice(device);
		setSensor(sensor);
		setValue(value);
		setLowLimit(lowLimit);
		setHighLimit(highLimit);
		setUnit(unit);
		setSamplingRate(samplingRate);
		setDate(date);

		this.displayName = null;

		// Fix Unit for Body Temperature and Temperature
		if ((sensor != null) && (sensor.equals("Temperature") || sensor.equals("BodyTemperature"))
				&& (unit != null) && unit.equals("C")) {
			this.unit = "&deg;C";
		}
	}	
	
	public Measurement(Device device, String sensor, String displayName, Double value,
			Double lowLimit, Double highLimit, String unit,
			Integer samplingRate, Date date) {
		this(device, sensor, value, lowLimit, highLimit, unit, samplingRate, date);
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
		
	@Column(name = "SENSOR", length=50)
	public String getSensor() {
		return sensor;
	}
	
	private void setSensor(String sensor) {
		this.sensor = sensor;
	}

	@Transient
	public String getDisplayName() {
		return displayName != null ? displayName : getDisplayName(sensor);
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

	@Column(name = "SAMPLINGRATE", length=20)
	@Type(type="integer_string")
	public Integer getSamplingRate() {
		return samplingRate;
	}
	
	private void setSamplingRate(Integer samplingRate) {
		this.samplingRate = samplingRate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATETIME", nullable=false)
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
	
	private void setConnected(Boolean connected) {
		if (connected == null) connected = Boolean.TRUE;
		this.connected = connected;
	}

	public void disconnect() {
		setConnected(false);
	}
	
	@Override
	@ManyToOne
	@JoinColumn(name="DEVICE_ID", nullable=false)
	@Cascade({CascadeType.SAVE_UPDATE})
	public Device getDevice() {
		return device;
	}
	
	private void setDevice(Device device) {
		this.device = device;
	}

	@Override
	public int compareTo(Measurement o) {
		// FIXME include ptuId
		return (o != null) ? getSensor().compareTo(o.getSensor()) : 1;
	}

	@Override
	public int hashCode() {
		return (getDevice() != null ? getDevice().hashCode() : 0)
				+ (getSensor() != null ? getSensor().hashCode() : 0)
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
			return (getDevice() == null ? m.getDevice() == null : getDevice()
					.equals(m.getDevice()))
					&& (getSensor() == null ? m.getSensor() == null : getSensor()
							.equals(m.getSensor()))
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
		return "Measurement(" + getDevice().getName() + "): sensor:" + getSensor() + ", value:"
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
