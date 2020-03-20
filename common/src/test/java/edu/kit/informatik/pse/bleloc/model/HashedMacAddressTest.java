package edu.kit.informatik.pse.bleloc.model;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class HashedMacAddressTest {
	private static final String StringValue = "0123456789abcdefdeadbeef00ff00ff00ff00ff00ff00ff00ff00ff00ffff00";

	private static final byte[] ByteArray = new byte[]{
		0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF, (byte) 0xDE, (byte) 0xAD,
		(byte) 0xBE, (byte) 0xEF, 0x00, (byte) 0xFF, 0x00, (byte) 0xFF, 0x00, (byte) 0xFF, 0x00, (byte) 0xFF, 0x00,
		(byte) 0xFF, 0x00, (byte) 0xFF, 0x00, (byte) 0xFF, 0x00, (byte) 0xFF, 0x00, (byte) 0xFF, (byte) 0xFF, 0x00,
		};

	private static final String DifferentStringValue = "0123456789abcdefdeadbeefff00ff00ff00ff00ff00ff00ff00ff00ff00ffff";

	@Test
	public void testWithString() {
		HashedMacAddress actual = HashedMacAddress.fromString(StringValue);

		Assert.assertEquals(StringValue, actual.toString());
		Assert.assertArrayEquals(ByteArray, actual.toByteArray());
	}

	@Test
	public void testWithByteArray() {
		HashedMacAddress actual = HashedMacAddress.fromByteArray(ByteArray);

		Assert.assertEquals(StringValue, actual.toString());
		Assert.assertArrayEquals(ByteArray, actual.toByteArray());
	}

	@Test
	public void testEquality() {
		HashedMacAddress fromString = HashedMacAddress.fromString(StringValue);
		HashedMacAddress fromSameString = HashedMacAddress.fromString(new String(StringValue));
		HashedMacAddress fromDifferentCaseString = HashedMacAddress.fromString(StringValue.toUpperCase());
		HashedMacAddress fromDifferentString = HashedMacAddress.fromString(DifferentStringValue);

		// The same string content should yield an equal object
		Assert.assertEquals(fromString, fromSameString);

		// The same string content only different case should still yield an equal object
		Assert.assertEquals(fromString, fromDifferentCaseString);

		// A different string content should yield an unequal object
		Assert.assertNotEquals(fromString, fromDifferentString);
	}
}