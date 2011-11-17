package ch.cern.atlas.apvs.dosimeter.server;

import java.util.Random;


public class Dosimeter {

	private int serialNo;
	private double dose;
	private double rate;
	private Random random = new Random(System.currentTimeMillis());

	// Conversion from device mrem into microsievert
	private static final double MICRO_SIEVERT = 10;

	public Dosimeter(int serialNo, double dose, double rate) {
		this.serialNo = serialNo;
		this.dose = dose;
		this.rate = rate;
	}

	
	public static String encode(Dosimeter dosimeter) {
		double rate = dosimeter.getRate() / MICRO_SIEVERT * 100;
		// FIXME...
		int ratePower = 0;
		String s = String.format("0%05d00%07.0f000%03.0f%1d0000000000000000000000", dosimeter.getSerialNo(), dosimeter.getDose(), rate, ratePower);
		int checksum = getChecksum(s);
		return s + String.format("%02X", checksum);
	}

	public static Dosimeter decode(String encodedString) {
		int serialNo = Integer.parseInt(encodedString.substring(0, 6));
		double dose = Double.parseDouble(encodedString.substring(8, 14) + "."
				+ encodedString.substring(14, 15))
				* MICRO_SIEVERT;
		double rate = Double.parseDouble(encodedString.substring(18, 19) + "."
				+ encodedString.substring(19, 21) + "E"
				+ encodedString.substring(21, 22))
				* MICRO_SIEVERT;
		
		int checksum = Integer.parseInt(encodedString.substring(44, 46), 16);
		if (getChecksum(encodedString) != checksum) return null;
		
		return new Dosimeter(serialNo, dose, rate);
	}

	public int getSerialNo() {
		return serialNo;
	}

	public double getDose() {
		return dose;
	}

	public double getRate() {
		return rate;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(getSerialNo()).hashCode()
				+ Double.valueOf(getDose()).hashCode()
				+ Double.valueOf(getRate()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Dosimeter) {
			Dosimeter d = (Dosimeter) obj;
			return (getSerialNo() == d.getSerialNo())
					&& (getDose() == d.getDose()) && (getRate() == d.getRate());
		}
		return super.equals(obj);
	}

	public String toString() {
		return "Dosimeter (" + getSerialNo() + "): dose=" + getDose()
				+ "; rate=" + getRate();
	}
	
	Dosimeter next() {
		double newDose = getDose() + getRate();
		double newRate = Math.max(0, getRate() + random.nextGaussian() * 0.5);
		return new Dosimeter(getSerialNo(), newDose, newRate);
	}
	
	private static int getChecksum(String encodedString) {
		System.err.println(encodedString);
		int checksum = 0;
		for (int i=0; i<Math.min(encodedString.length(), 44); i+=2) {
			checksum += Integer.parseInt(encodedString.substring(i, i+2), 16);
		}
		checksum = checksum & 0xFF;
		checksum = 0x100 - checksum;
		checksum = checksum & 0xFF;
		return checksum;
	}
}
