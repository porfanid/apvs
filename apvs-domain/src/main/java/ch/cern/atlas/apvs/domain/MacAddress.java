package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MacAddress implements Serializable, IsSerializable {

	private static final long serialVersionUID = 2378438435776354265L;
	private byte[] mac;

	protected MacAddress() {
		// serializable
	}
	
	public MacAddress(String s) {
		String[] tokens = s.split(":");
		if (tokens.length != 6)
			throw new IllegalArgumentException("mac should be six bytes");

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

	private int hex(char c) {
		if (c >= 'a' && c <= 'z')
			return (c - 'a') + 10;

		if (c >= 'A' && c <= 'Z')
			return (c - 'A') + 10;

		if (c >= '0' && c <= '9')
			return c - '0';

		throw new IllegalArgumentException("invalid hex char: " + c);
	}

	public MacAddress(byte[] mac) {
		if (mac == null || mac.length != 6)
			throw new IllegalArgumentException("mac should be six bytes");

		this.mac = mac;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(mac);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MacAddress other = (MacAddress) obj;
		if (!Arrays.equals(mac, other.mac))
			return false;
		return true;
	}

	public byte[] getBytes() {
		return mac;
	}

	@Override
	public String toString() {
		return toString(mac[0]) + ":" + toString(mac[1]) + ":"
				+ toString(mac[2]) + ":" + toString(mac[3]) + ":"
				+ toString(mac[4]) + ":" + toString(mac[5]);
	}

	private String toString(byte b) {
		final char[] hex = new String("0123456789ABCDEF").toCharArray();
		return "" + hex[(b & 0xF0) >> 4] + hex[(b & 0x0F)];
	}

}