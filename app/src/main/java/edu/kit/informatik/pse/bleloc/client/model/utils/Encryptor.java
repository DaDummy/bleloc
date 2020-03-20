package edu.kit.informatik.pse.bleloc.client.model.utils;

import edu.kit.informatik.pse.bleloc.client.model.device.Location;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import edu.kit.informatik.pse.bleloc.payload.Device;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * The Encryptor utility class encapsulates and hides the specific cryptographic algorithms. It is responsible for
 * packing and unpacking data. That is creating an encrypted data package as
 * <code>byte[]</code> from objects. Or reversing the process and decrypt a package to return
 * object instances.
 */
public class Encryptor {

	static private final byte[] salt = "bleloc".getBytes();
	static private final int ivSize = 12;
	static private final int tagSize = 16;

	private Encryptor() {
		// static utility class
	}

	private static GCMBlockCipher initAESGCM(byte[] key, boolean forEncryption, byte[] iv, final byte[] authData) {
		final int tagLenBits = 8 * tagSize;
		AESEngine cipher = new AESEngine();
		cipher.init(forEncryption, new KeyParameter(key));
		GCMBlockCipher gcm = new GCMBlockCipher(cipher);
		gcm.init(forEncryption, new AEADParameters(new KeyParameter(key), tagLenBits, iv, authData));
		return gcm;
	}

	public static byte[] encrypt(byte[] key, byte[] in) {
		try {
			byte[] iv = new byte[ivSize];
			new SecureRandom().nextBytes(iv);
			GCMBlockCipher cipher = initAESGCM(key, true, iv, new byte[0]);
			byte[] out = new byte[ivSize + in.length + tagSize];
			System.arraycopy(iv, 0, out, 0, iv.length);
			int extra = cipher.processBytes(in, 0, in.length, out, iv.length);
			cipher.doFinal(out, iv.length + extra);
			return out;
		} catch (InvalidCipherTextException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] decrypt(byte[] key, byte[] in) throws InvalidCipherTextException {
		GCMBlockCipher cipher = initAESGCM(key, false, Arrays.copyOfRange(in, 0, ivSize), new byte[0]);
		byte[] out = new byte[in.length - ivSize - tagSize];
		int extra = cipher.processBytes(in, ivSize, in.length - ivSize, out, 0);
		cipher.doFinal(out, extra);
		return out;
	}

	public static byte[] keyFromAddress(byte[] macAddress) {
		PKCS5S2ParametersGenerator pbe = new PKCS5S2ParametersGenerator();
		pbe.init(macAddress, salt, 2);
		return ((KeyParameter)pbe.generateDerivedParameters(128)).getKey();
	}

	public static HashedMacAddress hashedMacAddressFromKey(byte[] key) {
		PKCS5S2ParametersGenerator pbe = new PKCS5S2ParametersGenerator();
		pbe.init(key, salt, 1);
		return HashedMacAddress.fromByteArray(((KeyParameter)pbe.generateDerivedParameters(Device.HardwareIdentifierByteLength * Byte.SIZE)).getKey());
	}

	// TODO(ca): Move this method into Location class
	private static byte[] packLocation(Location location) {
		ByteBuffer buf = ByteBuffer.allocate(20);
		buf.putLong(location.getDate().getTime());
		buf.putInt(location.getIntLongitude());
		buf.putInt(location.getIntLatitude());
		buf.putInt(location.getSignalStrength());
		return buf.array();
	}

	// TODO(ca): Move this method into Location class
	private static Location unpackLocation(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		Location location = new Location();
		location.setDate(new Date(buf.getLong()));
		location.setIntLongitude(buf.getInt());
		location.setIntLatitude(buf.getInt());
		location.setSignalStrength(buf.getInt());
		return location;
	}

	/**
	 * Packs and encrypts location data.
	 *
	 * @param hardwareIdentifier
	 * 		The hardware identifier to use as key.
	 * @param location
	 * 		The location data to pack and encrypt
	 * @return The packed and encrypted location data.
	 */
	public static byte[] encryptScanResult(byte[] hardwareIdentifier, Location location) {
		return encrypt(keyFromAddress(hardwareIdentifier), packLocation(location));
	}

	/**
	 * Decrypts and unpacks location data.
	 *
	 * @param hardwareIdentifier
	 * 		The hardware identifier to use as key.
	 * @param data
	 * 		The packed and encrypted location data
	 * @return The decrypted location data.
	 */
	public static Location decryptScanResult(byte[] hardwareIdentifier, byte[] data) throws InvalidCipherTextException {
		return unpackLocation(decrypt(keyFromAddress(hardwareIdentifier), data));
	}
}
