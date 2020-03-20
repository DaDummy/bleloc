package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class LoginCredentialsPayloadTest {
	private static final String testName = "Alice";
	private static final String testPassword = "123456";

	@Test
	public void testCostructor() {
		LoginCredentialsPayload payload = new LoginCredentialsPayload(
			testName, testPassword
		);

		Assert.assertEquals(testName, payload.getName());
		Assert.assertEquals(testPassword, payload.getPassword());
	}

	@Test
	public void testGettersAndSetters() {
		LoginCredentialsPayload payload = new LoginCredentialsPayload();

		payload.setName(testName);
		payload.setPassword(testPassword);

		Assert.assertEquals(testName, payload.getName());
		Assert.assertEquals(testPassword, payload.getPassword());
	}
}