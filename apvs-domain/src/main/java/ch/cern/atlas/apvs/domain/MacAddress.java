import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a MAC address (Media Access Control address) and provides utility methods for handling MAC addresses.
 * This class is immutable, ensuring that instances cannot be modified after creation.
 */
public class MacAddress implements Serializable {

	private static final long serialVersionUID = 2378438435776354265L;
	private final byte[] mac;
	

	/**
     * Constructs a MacAddress instance from a MAC address string.
     *
     * @param s The MAC address string in the format "XX:XX:XX:XX:XX:XX".
     * @throws IllegalArgumentException If the input string is not a valid MAC address.
     */
	public MacAddress(String s) {

		if(!MacAddress.isValidMacAddress(s)){
			throw new IllegalArgumentException("Invalid Mac Address");
		}

		String[] tokens = s.split(":");

		byte[] b = new byte[6];
		int i = 0;
		for (String token : tokens) {
			if (token.length() != 2)
				throw new IllegalArgumentException("invalid mac token: "
						+ token);

			char c0 = token.charAt(0);
			char c1 = token.charAt(1);
			b[i++] = (byte) (hex(c0) * 16 + hex(c1));
		}

		mac = b;
	}


	/**
     * Constructs a MacAddress instance from a byte array representing a MAC address.
     *
     * @param mac The byte array representing a MAC address. It must have a length of 6 bytes.
     * @throws IllegalArgumentException If the input byte array is null or not of length 6.
     */
	public MacAddress(byte[] mac) {
		if (mac == null || mac.length != 6)
			throw new IllegalArgumentException("mac should be six bytes");

		this.mac = mac;
	}

	private int hex(char c) {
		if (c >= 'a' && c <= 'z')
			return (c - 'a') + 10;

		if (c >= 'A' && c <= 'Z')
			return (c - 'A') + 10;

		if (c >= '0' && c <= '9')
			return c - '0';

		throw new IllegalArgumentException("invalid hex char: " + c);
	}


	/**
     * Calculates the hash code for this MacAddress instance.
     *
     * @return The hash code.
     */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(mac);
		return result;
	}


	/**
     * Compares this MacAddress instance to another object for equality.
     *
     * @param obj The object to compare.
     * @return true if the objects are equal, false otherwise.
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MacAddress other = (MacAddress) obj;


		return Arrays.equals(mac, other.mac);
	}


	/**
     * Gets the byte array representation of this MacAddress instance.
     *
     * @return A copy of the internal byte array.
     */
	public byte[] getBytes() {
		// Return a copy of the internal array to maintain immutability
		return Arrays.copyOf(mac, mac.length);
	}


	/**
     * Returns a string representation of this MacAddress in the format "XX:XX:XX:XX:XX:XX".
     *
     * @return The string representation of this MacAddress.
     */
	@Override
	public String toString() {
		return String.format("%02X:%02X:%02X:%02X:%02X:%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
	}

	private String toString(byte b) {
		return String.format("%02X", b);
	}


	/**
     * Checks whether a given string is a valid MAC address in the format "XX:XX:XX:XX:XX:XX".
     *
     * @param s The string to check.
     * @return true if the input string is a valid MAC address, false otherwise.
     */
	private static boolean isValidMacAddress(String s) {
		String MAC_PATTERN = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        Pattern pattern = Pattern.compile(MAC_PATTERN);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
}