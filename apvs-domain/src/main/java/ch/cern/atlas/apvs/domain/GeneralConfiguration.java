package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class GeneralConfiguration implements Message, Serializable,
		IsSerializable, Comparable<GeneralConfiguration> {

	private static final long serialVersionUID = 4796032680266987232L;

	private volatile Device device;
	private String type = "GeneralConfiguration";
	private String dosimeterId;
	private String bssid;

	protected GeneralConfiguration() {
	}

	public GeneralConfiguration(Device device, String dosimeterId, String bssid) {
		this.device = device;
		this.dosimeterId = dosimeterId;
		this.bssid = bssid;
	}

	@Override
	public Device getDevice() {
		return device;
	}

	public String getDosimeterId() {
		return dosimeterId;
	}
	
	public String getBSSID() {
		return bssid;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public int compareTo(GeneralConfiguration o) {
		return (o != null) && (getDosimeterId() != null) ? getDosimeterId()
				.compareTo(o.getDosimeterId()) : 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bssid == null) ? 0 : bssid.hashCode());
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		result = prime * result
				+ ((dosimeterId == null) ? 0 : dosimeterId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		GeneralConfiguration other = (GeneralConfiguration) obj;
		if (bssid == null) {
			if (other.bssid != null) {
				return false;
			}
		} else if (!bssid.equals(other.bssid)) {
			return false;
		}
		if (device == null) {
			if (other.device != null) {
				return false;
			}
		} else if (!device.equals(other.device)) {
			return false;
		}
		if (dosimeterId == null) {
			if (other.dosimeterId != null) {
				return false;
			}
		} else if (!dosimeterId.equals(other.dosimeterId)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GeneralConfiguration [device=" + device.getName() + ", type=" + type
				+ ", dosimeterId=" + dosimeterId + ", bssid=" + bssid + "]";
	}
}
