package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChangePasswordRequestPayloadTest {
	private static final String testOldPassword = "123456";
	private static final String testNewPassword = "987654";

	@Test
	public void testConstructer() {
		ChangePasswordRequestPayload payload = new ChangePasswordRequestPayload(
			testOldPassword, testNewPassword
		);

		Assert.assertEquals(testOldPassword, payload.getOldPassword());
		Assert.assertEquals(testNewPassword, payload.getNewPassword());
	}

	@Test
	public void testGetterAndSetter() {
		ChangePasswordRequestPayload payload = new ChangePasswordRequestPayload();

		payload.setOldPassword(testOldPassword);
		payload.setNewPassword(testNewPassword);

		Assert.assertEquals(testOldPassword, payload.getOldPassword());
		Assert.assertEquals(testNewPassword, payload.getNewPassword());
	}
}