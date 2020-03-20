package edu.kit.informatik.pse.bleloc.model;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class WebTokenAuthenticatorFactoryTest {

	@Test
	public void createAuthenticator() {
		Assert.assertNotNull(new WebTokenAuthenticatorFactory()
			                     .createAuthenticator(AuthenticatorPurpose.UserAccountAuthentication, UserAccount.InvalidId));
		Assert.assertNotNull(new WebTokenAuthenticatorFactory()
			                     .createAuthenticator(AuthenticatorPurpose.BackofficeAccountAuthentication, BackofficeAccount.InvalidId));
	}
}