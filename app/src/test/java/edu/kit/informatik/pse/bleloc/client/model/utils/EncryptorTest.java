package edu.kit.informatik.pse.bleloc.client.model.utils;

import edu.kit.informatik.pse.bleloc.client.model.device.Location;
import edu.kit.informatik.pse.bleloc.client.model.utils.Encryptor;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.Assert;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

public class EncryptorTest {
	static final private byte[] mac = new byte[]{2,4,6,8,10,12};
	private byte[] key = new byte[16];
	private byte[] key2 = new byte[16];
	private byte[] data = new byte[16];

	public EncryptorTest() {
		SecureRandom random = new SecureRandom();
		random.nextBytes(key);
		random.nextBytes(key2);
		random.nextBytes(data);
	}

	@Test
	public void testEncAndDec() throws InvalidCipherTextException {
		byte[] res = Encryptor.encrypt(key, data);
		byte[] orig = Encryptor.decrypt(key, res);
		Assert.assertArrayEquals(data, orig);
	}

	@Test(expected = InvalidCipherTextException.class)
	public void testManipulated() throws InvalidCipherTextException {
		byte[] res = Encryptor.encrypt(key, data);
		res[0]++;
		Encryptor.decrypt(key, res);
	}

	@Test(expected = InvalidCipherTextException.class)
	public void testWrongKey() throws InvalidCipherTextException {
		byte[] res = Encryptor.encrypt(key, data);
		Encryptor.decrypt(key2, res);
	}

	@Test
	public void testHashedMacFromAddress() {
		byte[] key = Encryptor.keyFromAddress(new byte[]{2,4,6,8,10,12});
		Encryptor.hashedMacAddressFromKey(key);
	}

	@Test
	public void testLocation() throws InvalidCipherTextException {
		Location l = new Location();
		Date d = new Date();
		l.setDate(d);
		l.setIntLongitude(42);
		l.setIntLatitude(1337);
		l.setSignalStrength(-23);
		byte[] enc = Encryptor.encryptScanResult(mac, l);
		Location l2 = Encryptor.decryptScanResult(mac, enc);
		Assert.assertEquals(l.getDate().getTime(), l2.getDate().getTime());
		Assert.assertEquals(l.getIntLongitude(), l2.getIntLongitude());
		Assert.assertEquals(l.getIntLatitude(), l2.getIntLatitude());
		Assert.assertEquals(l.getSignalStrength(), l2.getSignalStrength());
	}
}
