package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
@Entity
@Table( name = "TBL_SENSORS" )
public class Sensor implements Serializable, IsSerializable {

	private static final long serialVersionUID = -7343635981745833982L;

	private int id;
	private Device device;
	private String name;
	private Boolean enabled;

	protected Sensor() {
	}

	public Sensor(Device device, String name, Boolean enabled) {
		setDevice(device);
		setName(name);
		setEnabled(enabled);
	}
	
	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}

	private void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setDevice(Device device) {
		this.device = device;
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name = "ID", length=15)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name="DEVICE_ID", nullable=false)
	public Device getDevice() {
		return device;
	}

	@Column(name = "SENSOR", length=50, nullable=false)
	public String getName() {
		return name;
	}

	@Column(name = "ENABLED")
	@Type(type="yes_no")
	public Boolean isEnabled() {
		return enabled;
	}

	@Override
	public String toString() {
		return "Sensor [id=" + id + ", device=" + device + ", name=" + name
				+ ", enabled=" + enabled + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Sensor other = (Sensor) obj;
		if (device == null) {
			if (other.device != null) {
				return false;
			}
		} else if (!device.equals(other.device)) {
			return false;
		}
		if (enabled != other.enabled) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
