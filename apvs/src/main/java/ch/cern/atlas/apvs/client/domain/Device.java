package ch.cern.atlas.apvs.client.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Device implements Serializable, IsSerializable {

	private static final long serialVersionUID = 849926551483611340L;

	private int id;
	
	@NotNull
	@Size(min = 2)
	private String name;
	
	private String ip;
	private String description;
	private String macAddress;
	private String hostName;
	
	public Device() {
		// Serialization
	}
	
	public Device(int id, String name, String ip, String description, String macAddress, String hostName) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.description = description;
		this.macAddress = macAddress;
		this.hostName = hostName;
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
	
	public String getMacAddress() {
		return macAddress;
	}
	
	public String getHostName() {
		return hostName;
	}
}
