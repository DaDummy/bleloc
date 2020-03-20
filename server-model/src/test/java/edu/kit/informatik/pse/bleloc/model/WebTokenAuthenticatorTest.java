package edu.kit.informatik.pse.bleloc.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WebTokenAuthenticatorTest {

	WebTokenAuthenticator userAuth;
	WebTokenAuthenticator backofficeAuth;

	@Before
	public void setUp() throws Exception {
		userAuth = new WebTokenAuthenticator(AuthenticatorPurpose.UserAccountAuthentication, UserAccount.InvalidId);
		backofficeAuth = new WebTokenAuthenticator(AuthenticatorPurpose.BackofficeAccountAuthentication, BackofficeAccount.InvalidId);
	}

	@Test
	public void validUserCookieFullCycle() {
		final long correctAccountId = 1337;
		String cookie = userAuth.createCookie(correctAccountId);
		Assert.assertNotNull(cookie);

		long accountId = userAuth.verifyCookie(cookie);
		Assert.assertEquals(correctAccountId, accountId);
	}

	@Test
	public void validBackendCookieFullCycle() {
		final long correctAccountId = 42;
		String cookie = backofficeAuth.createCookie(correctAccountId);
		Assert.assertNotNull(cookie);

		long accountId = backofficeAuth.verifyCookie(cookie);
		Assert.assertEquals(correctAccountId, accountId);
	}

	@Test
	public void verifyImpersonationAttackCookie() {
		final long userAccountId = 1337;
		String cookie = userAuth.createCookie(userAccountId);
		Assert.assertNotNull(cookie);

		long accountId = backofficeAuth.verifyCookie(cookie);
		Assert.assertEquals(BackofficeAccount.InvalidId, accountId);
	}

	@Test
	public void verifyEmptyCookie() {
		String cookie = "";
		Long accountId = userAuth.verifyCookie(cookie);
		Assert.assertEquals(UserAccount.InvalidId, accountId);
	}

	@Test
	public void verifyNullCookie() {
		String cookie = null;
		Long accountId = userAuth.verifyCookie(cookie);
		Assert.assertEquals(UserAccount.InvalidId, accountId);
	}

	@Test
	public void verifyInvalidCookie() {
		String cookie = "invalid";
		Long accountId = userAuth.verifyCookie(cookie);
		Assert.assertEquals(UserAccount.InvalidId, accountId);
	}
}