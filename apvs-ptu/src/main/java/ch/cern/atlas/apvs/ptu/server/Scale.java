package ch.cern.atlas.apvs.ptu.server;

public class Scale {

	public static Number getHighLimit(Number high, String unit) {
		if ((unit.equals("mSv") || unit.equals("mSv/h")) && (high != null)) {
			return high.doubleValue() * 1000;
		}
		return high;
	}

	public static Number getLowLimit(Number low, String unit) {
		if ((unit.equals("mSv") || unit.equals("mSv/h")) && (low != null)) {
			return low.doubleValue() * 1000;
		}
		return low;
	}

	public static Number getValue(Number value, String unit) {
		if ((unit.equals("mSv") || unit.equals("mSv/h")) && (value != null)) {
			return value.doubleValue() * 1000;
		}
		return value;
	}

	public static String getUnit(String unit) {
		if (unit != null) {
			if (unit.equals("mSv")) {
				return "&micro;Sv";
			}
			if (unit.equals("mSv/h")) {
				return "&micro;Sv/h";
			}
		}
		return unit;
	}

}
