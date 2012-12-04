package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

public class History implements Serializable {

	private static final long serialVersionUID = 2802095781867809709L;

	private String ptuId;
	private String name;
	private Integer samplingRate;
	private Number[][] data;
	private int index;
	private String unit;

	private final static int INITIAL_CAPACITY = 200;

	public History() {
	}

	public History(String ptuId, String name, Integer samplingRate, String unit) {
		this.ptuId = ptuId;
		this.name = name;
		this.samplingRate = samplingRate;
		this.unit = unit;
		this.data = new Number[INITIAL_CAPACITY][2];
		index = 0;
	}

	public String getPtuId() {
		return ptuId;
	}

	public String getName() {
		return name;
	}

	public Number[][] getData() {
		Number[][] result = new Number[index][2];

		System.arraycopy(data, 0, result, 0, index);
		return result;
	}

	public String getUnit() {
		return unit;
	}

	public boolean addEntry(long time, Number value) {
		if (index >= data.length) {
			Number[][] newData = new Number[data.length * 2][2];			
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
		}

		data[index][0] = time;
		data[index][1] = value;
		index++;

		return true;
	}

	public Measurement getMeasurement() {
		return index == 0 ? null : new Measurement(ptuId, name,
				data[index - 1][1], samplingRate, unit, new Date(
						data[index - 1][0].longValue()));
	}

	public int getSize() {
		return index;
	}
}
