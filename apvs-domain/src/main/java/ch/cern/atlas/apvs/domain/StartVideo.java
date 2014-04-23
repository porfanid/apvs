package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class StartVideo implements Message, Serializable, IsSerializable {

	private static final long serialVersionUID = 6751105975861927018L;

	private volatile Device device;
	private String type = "StartRecording";

	protected StartVideo() {
	}

	public StartVideo(Device device) {
		this.device = device;
	}

	@Override
	public Device getDevice() {
		return device;
	}

	@Override
	public String getType() {
		return type;
	}
}
