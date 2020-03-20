package edu.kit.informatik.pse.bleloc.model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Arrays;

/**
 * Utility class provides helper methods for hashing passwords
 */
class PasswordUtil {

	private PasswordUtil() {}

	/**
	 * Hashes a password with salt.
	 * @param password Password to be hashed
	 * @param passwordSalt Salt to be used
	 * @return String representing the hashed and salted password
	 */
	static String hashPassword(String password, String passwordSalt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(passwordSalt.getBytes("UTF-8"));

			byte[] hashedPassword =  md.digest(password.getBytes());
			return Arrays.toString(hashedPassword);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Generates a random password salt
	 * @param length length of the salt
	 * @return String representing the salt
	 */
	static String generateRandomSalt(int length) {
		if (length <= 0 || length >= 4096) {
			throw new IllegalArgumentException("Salt Length should be between 0 and 4096");
		}
		byte[] salt = new byte[length];
		new Random().nextBytes(salt);
		return salt.toString();
	}

	/**
	 * Generates a random password salt with length 256
	 * @return String representing the salt
	 */
	static String generateRandomSalt() {
		return PasswordUtil.generateRandomSalt(256);
	}
}
