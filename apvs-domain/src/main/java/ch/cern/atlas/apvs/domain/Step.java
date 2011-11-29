package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

public class Step implements Serializable {

	private static final long serialVersionUID = -1388827398323128166L;
	private String name;
	private String video;

	public Step() {
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
