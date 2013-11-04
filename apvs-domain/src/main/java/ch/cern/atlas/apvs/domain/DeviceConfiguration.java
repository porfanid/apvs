package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DeviceConfiguration implements Serializable, IsSerializable {

	private static final long serialVersionUID = -1523917602976642818L;

	private Map<Device, GeneralConfiguration> generalConfiguration;
	private Map<String, Integer> measurementConfigurationIndex;
	
	private List<MeasurementConfiguration> measurementConfiguration;
	
	public DeviceConfiguration() {
		generalConfiguration = new HashMap<Device, GeneralConfiguration>();
		measurementConfigurationIndex = new HashMap<String, Integer>();
		
		measurementConfiguration = new ArrayList<MeasurementConfiguration>();
	}

	public void add(GeneralConfiguration gc) {
		generalConfiguration.put(gc.getDevice(), gc);
	}
	
	public void add(MeasurementConfiguration mc) {
		int index = index(mc);
		if (index < 0) {
			measurementConfigurationIndex.put(key(mc), measurementConfiguration.size());
			measurementConfiguration.add(mc);
		} else {
			measurementConfiguration.set(index, mc);
		}
	}
	
	public List<MeasurementConfiguration> getMeasurementConfiguration() {
		return measurementConfiguration;
	}
	
	public GeneralConfiguration getGeneralConfiguration(Device device) {
		return generalConfiguration.get(device);
	}
	
	private int index(MeasurementConfiguration mc) {
		Integer index = measurementConfigurationIndex.get(key(mc));
		return index == null ? -1 : index;
	}
	
	private String key(MeasurementConfiguration mc) {
		return  mc.getDevice().getName()+":"+mc.getSensor();
	}
}
