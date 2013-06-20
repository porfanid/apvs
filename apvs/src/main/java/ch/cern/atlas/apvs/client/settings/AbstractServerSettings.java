package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public abstract class AbstractServerSettings implements Serializable, IsSerializable {

	private static final long serialVersionUID = 5654940808477419548L;

	private Map<String, String> map = new HashMap<String, String>();

	public AbstractServerSettings() {
		super();
	}

	public String get(String name) {
		return map.get(name);
	}

	public String put(String name, String value) {
		return map.put(name, value);
	}

	public String put(String name, boolean value) {
		return map.put(name, Boolean.toString(value));
	}

}