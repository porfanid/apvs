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

	public GeneralConfiguration() {
	}

	public GeneralConfiguration(Device device, String dosimeterId) {
		this.device = device;
		this.dosimeterId = dosimeterId;
	}

	@Override
	public Device getDevice() {
		return device;
	}

	public String getDosimeterId() {
		return dosimeterId;
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
		return (getDevice() != null ? getDevice().hashCode() : 0)
				+ (getDosimeterId() != null ? getDosimeterId().hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof GeneralConfiguration)) {
			GeneralConfiguration m = (GeneralConfiguration) obj;
			return (getDevice() == null ? m.getDevice() == null : getDevice()
					.equals(m.getDevice()))
					&& (getDosimeterId() == null ? m.getDosimeterId() == null
							: getDosimeterId().equals(m.getDosimeterId()));
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "GeneralConfiguration(" + getDevice().getName() + "): dosimeterId="
				+ getDosimeterId();
	}
}
