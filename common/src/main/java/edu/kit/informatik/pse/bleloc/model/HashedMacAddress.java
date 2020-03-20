package edu.kit.informatik.pse.bleloc.model;

import java.util.Arrays;

/**
 * Representation for a hardware identifier.
 */
public class HashedMacAddress {
	private static final int HexRadix = 16;

	private final byte[] byteArray;

	/**
	 * Creates a hashed identifier from a String input.
	 *
	 * @param input
	 * 		the hashed identifier
	 */
	public static HashedMacAddress fromString(String input) {
		int len = input.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(input.charAt(i), HexRadix) << 4) +
			                      Character.digit(input.charAt(i + 1), HexRadix));
		}
		return new HashedMacAddress(data);
	}

	/**
	 * Creates a hashed identifier from a byte array input.
	 *
	 * @param input
	 * 		the hashed identifier
	 */
	public static HashedMacAddress fromByteArray(byte[] input) {
		return new HashedMacAddress(input);
	}

	/**
	 * Converts from a hashed identifier back to a string
	 */
	@Override
	public String toString() {
		char[] hexChars = new char[byteArray.length * 2];
		for (int j = 0; j < byteArray.length; j++) {
			int v = byteArray[j] & 0xFF;

			hexChars[j * 2] = Character.forDigit(v >>> 4, HexRadix);
			hexChars[j * 2 + 1] = Character.forDigit(v & 0x0F, HexRadix);
		}
		return new String(hexChars);
	}

	/**
	 * Converts from a hashed identifier back to a byte array
	 */
	public byte[] toByteArray() {
		return byteArray;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof  HashedMacAddress) && Arrays.equals(byteArray, ((HashedMacAddress) o).byteArray);
	}

	private HashedMacAddress(byte[] byteArray) {
		this.byteArray = byteArray;
	}
}
