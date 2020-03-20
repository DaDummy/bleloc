package edu.kit.informatik.pse.bleloc.model;

import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Testing the correctness of the hashing
 */
public class PasswordUtilTest {

	/**
	 * Same password, same salt, expect same hash
	 */
	@Test
	public void hashSamePasswordTest() {
		String password = "foo";
		String passwordSalt = "123456789";

		String result1 = PasswordUtil.hashPassword(password, passwordSalt);
		String result2 = PasswordUtil.hashPassword(password, passwordSalt);

		Assert.assertEquals(result1, result2);
	}

	/**
	 * Different Passwords, expect different hashs
	 */
	@Test
	public  void hashDiffPasswordTest() {
		String password1 = "foo";
		String password2 = "bar";
		String passwordSalt = "123456789";

		String result1 = PasswordUtil.hashPassword(password1, passwordSalt);
		String result2 = PasswordUtil.hashPassword(password2, passwordSalt);

		Assert.assertNotEquals(result1, result2);
	}

	/**
	 * Same Passwords, different salts, expect different hashs
	 */
	@Test
	public  void hashDiffSaltTest() {
		String password = "foo";
		String passwordSalt1 = "987654321";
		String passwordSalt2 = "123456789";

		String result1 = PasswordUtil.hashPassword(password, passwordSalt1);
		String result2 = PasswordUtil.hashPassword(password, passwordSalt2);

		Assert.assertNotEquals(result1, result2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateRandomSalt_illegalLength() {
		String salt = PasswordUtil.generateRandomSalt(5000);
	}

	@Test
	public void generateRandomSalt() {
		Assert.assertTrue(PasswordUtil.generateRandomSalt().length() > 0);
	}

}