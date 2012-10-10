package ch.cern.atlas.apvs.client.ui;

import java.io.Serializable;

public class Device implements Serializable {

	private static final long serialVersionUID = 849926551483611340L;

	private int id;
	private String name;
	private String ip;
	private String description;
	
	public Device() {
		// Serialization
	}
	
	public Device(int id, String name, String ip, String description) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	public String getDescription() {
		return description;
	}
}
