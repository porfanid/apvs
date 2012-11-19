package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

public class History implements Serializable {

	private static final long serialVersionUID = 2802095781867809709L;

	private String ptuId;
	private String name;
	private Integer samplingRate;
	private Number[][] data;
	private String unit;
	
	public History() {
	}
	
	public History(String ptuId, String name, Integer samplingRate, Number[][] data, String unit) {
		this.ptuId = ptuId;
		this.name = name;
		this.samplingRate = samplingRate;
		this.data = data;
		this.unit = unit;
	}
	
	public String getPtuId() {
		return ptuId;
	}
	
	public String getName() {
		return name;
	}

	public Number[][] getData() {
		return data;
	}
	
	public String getUnit() {
		return unit;
	}

	public void addEntry(long time, Number value) {
		int lastIndex = data.length - 1;
		
		// check that entry does not exist yet
		if (data[lastIndex][0].equals(time) && data[lastIndex][1].equals(value)) {
			return;
		}
		
		// move array by on entry
		System.arraycopy(data, 1, data, 0, lastIndex);
		
		// copy new value in
		data[lastIndex][0] = time;
		data[lastIndex][1] = value;
	}

	public Measurement getMeasurement() {
		int lastIndex = data.length - 1;		
		return new Measurement(ptuId, name, data[lastIndex][1], samplingRate, unit, new Date(data[lastIndex][0].longValue()));
	}
}
