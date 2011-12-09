package ch.cern.atlas.apvs.dosimeter.server;

import ch.cern.atlas.apvs.domain.Dosimeter;

public class DosimeterCoder {

	// Conversion from device mrem into microsievert
	private static final double MICRO_SIEVERT = 10;

	private DosimeterCoder() {
	}

	public static String encode(Dosimeter dosimeter) {
		double rate = dosimeter.getRate() / MICRO_SIEVERT * 100;
		// FIXME...
		int ratePower = 0;
		String s = String.format(
				"0%05d00%07.0f000%03.0f%1d0000000000000000000000",
				dosimeter.getSerialNo(), dosimeter.getDose(), rate, ratePower);
		int checksum = getChecksum(s);
		return s + String.format("%02X", checksum);
	}

	public static Dosimeter decode(String encodedString) {
		System.err.println("'"+encodedString+"' "+encodedString.length());
		if (encodedString.length() < 46) return null;
		
		int serialNo = Integer.parseInt(encodedString.substring(0, 6));
		double dose = Double.parseDouble(encodedString.substring(8, 14) + "."
				+ encodedString.substring(14, 15))
				* MICRO_SIEVERT;
		double rate = Double.parseDouble(encodedString.substring(18, 19) + "."
				+ encodedString.substring(19, 21) + "E"
				+ encodedString.substring(21, 22))
				* MICRO_SIEVERT;

		int checksum = Integer.parseInt(encodedString.substring(44, 46), 16);
		if (getChecksum(encodedString) != checksum) {
			return null;
		}

		return new Dosimeter(serialNo, dose, rate);
	}

	private static int getChecksum(String encodedString) {
		int checksum = 0;
		for (int i = 0; i < Math.min(encodedString.length(), 44); i += 2) {
			checksum += Integer.parseInt(encodedString.substring(i, i + 2), 16);
		}
		checksum = checksum & 0xFF;
		checksum = 0x100 - checksum;
		checksum = checksum & 0xFF;
		return checksum;
	}

}
