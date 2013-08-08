package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
@Entity
@Table( name = "TBL_DEVICES" )
public class Device implements Serializable, IsSerializable {

	private static final long serialVersionUID = 849926551483611340L;

	private int id;
	
	@NotNull
	@Size(min = 2)
	private String name;
	
	private InetAddress ip;
	private String description;
	private MacAddress macAddress;
	private String hostName;
	
	public Device() {
		// Serialization
	}
	
	// FIXME can be removed if we use devices everywhere
	public Device(String name) {
		this(name, null, null, null, null);
	}
	
	// FIXME can be removed when using hibernate
	public Device(int id, String name, InetAddress ip, String description, MacAddress macAddress, String hostName) {
		this(name, ip, description, macAddress, hostName);
		setId(id);
	}
	
	public Device(String name, InetAddress ip, String description, MacAddress macAddress, String hostName) {
		setName(name);
		setIp(ip);
		setDescription(description);
		setMacAddress(macAddress);
		setHostName(hostName);
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name = "ID", length=3)
	public int getId() {
		return id;
	}
	
	private void setId(int id) {
		this.id = id;
	}

	@Column(name = "NAME", length=20)
	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}

	@Column(name = "IP", length=20)
	@Type(type="inetaddress")
	public InetAddress getIp() {
		return ip;
	}
	
	private void setIp(InetAddress ip) {
		this.ip = ip;
	}

	@Column(name = "DSCR", length=500)
	public String getDescription() {
		return description;
	}
	
	private void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "MAC_ADDR", length=50)
	@Type(type="macaddress")
	public MacAddress getMacAddress() {
		return macAddress;
	}
	
	private void setMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
	}
	
	@Column(name = "HOST_NAME", length=50)
	public String getHostName() {
		return hostName;
	}
	
	private void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result
				+ ((macAddress == null) ? 0 : macAddress.hashCode());
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
		Device other = (Device) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (hostName == null) {
			if (other.hostName != null) {
				return false;
			}
		} else if (!hostName.equals(other.hostName)) {
			return false;
		}
		if (ip == null) {
			if (other.ip != null) {
				return false;
			}
		} else if (!ip.equals(other.ip)) {
			return false;
		}
		if (macAddress == null) {
			if (other.macAddress != null) {
				return false;
			}
		} else if (!macAddress.equals(other.macAddress)) {
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
