package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Step implements Serializable, IsSerializable {

	private static final long serialVersionUID = -1388827398323128166L;
	private String name;
	private String video;

	protected Step() {
	}
	
	public Step(String name, String video) {
		this.name = name;
		this.video = video;
	}
		
	public String getName() {
		return name;
	}
	
	public String getVideo() {
		return video;
	}
}
