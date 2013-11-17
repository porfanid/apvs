package ch.cern.atlas.apvs.db;

public class Scale {

	public static Double getUpThreshold(Double high, String unit) {
		if ((unit != null) && (unit.equals("mSv") || unit.equals("mSv/h")) && (high != null)) {
			return high.doubleValue() * 1000;
		}
		return high;
	}

	public static Double getDownThreshold(Double low, String unit) {
		if ((unit != null) && (unit.equals("mSv") || unit.equals("mSv/h")) && (low != null)) {
			return low.doubleValue() * 1000;
		}
		return low;
	}

	public static Double getValue(Double value, String unit) {
		if ((unit != null) && (unit.equals("mSv") || unit.equals("mSv/h")) && (value != null)) {
			return value.doubleValue() * 1000;
		}
		return value;
	}

	public static String getUnit(String sensor, String unit) {
		if (unit != null) {
			if (unit.equals("mSv")) {
				return "&micro;Sv";
			}
			if (unit.equals("mSv/h")) {
				return "&micro;Sv/h";
			}
			if ((sensor.equals("Temperature") || sensor
					.equals("BodyTemperature")) && (unit.equals("C"))) {
				return "&deg;C";
			}
		}
		return unit;
	}

}
