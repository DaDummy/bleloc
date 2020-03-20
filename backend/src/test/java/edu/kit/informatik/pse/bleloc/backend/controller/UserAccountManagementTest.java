package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.cdi.UserAccountProxy;
import edu.kit.informatik.pse.bleloc.model.UserAccount;
import edu.kit.informatik.pse.bleloc.model.UserAccountStore;
import edu.kit.informatik.pse.bleloc.payload.ChangePasswordRequestPayload;
import edu.kit.informatik.pse.bleloc.payload.LoginCredentialsPayload;
import edu.kit.informatik.pse.bleloc.requestfilters.RequireUserAccountFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

public class UserAccountManagementTest extends ControllerTest {
	private static final String User2Name = "user2";
	private static final String User2Password = "uiuiui";
	private static final String WrongPassword = "lalalalala";

	private UserAccountManagement resource;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		resource = new UserAccountManagement();
		resource.em = em;
		resource.request = generateFakeRequest();
	}

	@Test
	public void postLoginOk() {
		LoginCredentialsPayload payload = new LoginCredentialsPayload(UserName, UserPassword);

		Response response = resource.postLogin(payload);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
		NewCookie cookie = response.getCookies().get(RequireUserAccountFilter.CookieName);
		Assert.assertEquals(userAccount.getId(), RequireUserAccountFilter.ActiveAuthenticator.verifyCookie(cookie.getValue()));

		Assert.assertEquals(NumberOfAccounts, (long)userAccountStore.getCount());
	}

	@Test
	public void postLoginBadUser() {
		LoginCredentialsPayload payload = new LoginCredentialsPayload(User2Name, UserPassword);

		Response response = resource.postLogin(payload);

		Assert.assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
		NewCookie cookie = response.getCookies().get(RequireUserAccountFilter.CookieName);
		Assert.assertNull(cookie);

		Assert.assertEquals(NumberOfAccounts, (long)userAccountStore.getCount());
	}

	@Test
	public void postLoginBadPassword() {
		LoginCredentialsPayload payload = new LoginCredentialsPayload(UserName, User2Password);

		Response response = resource.postLogin(payload);

		Assert.assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
		NewCookie cookie = response.getCookies().get(RequireUserAccountFilter.CookieName);
		Assert.assertNull(cookie);

		Assert.assertEquals(NumberOfAccounts, (long)userAccountStore.getCount());
	}

	@Test
	public void postRegisterOk() {
		LoginCredentialsPayload payload = new LoginCredentialsPayload(User2Name, User2Password);

		Response response = resource.postRegister(payload);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
		NewCookie cookie = response.getCookies().get(RequireUserAccountFilter.CookieName);
		long newUserId = RequireUserAccountFilter.ActiveAuthenticator.verifyCookie(cookie.getValue());
		Assert.assertNotEquals((long) userAccount.getId(), newUserId);

		Assert.assertEquals(NumberOfAccounts + 1, (long)userAccountStore.getCount());
		UserAccount newUserAccount = userAccountStore.get(newUserId);
		Assert.assertNotNull(newUserAccount);
		Assert.assertEquals(payload.getName(), newUserAccount.getName());
		Assert.assertTrue(newUserAccount.verifyPassword(payload.getPassword()));
	}

	@Test
	public void postRegisterEmtpyName() {
		LoginCredentialsPayload payload = new LoginCredentialsPayload("", User2Password);

		Response response = resource.postRegister(payload);

		Assert.assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
		NewCookie cookie = response.getCookies().get(RequireUserAccountFilter.CookieName);
		Assert.assertNull(cookie);

		Assert.assertEquals(NumberOfAccounts, (long)userAccountStore.getCount());
	}

	@Test
	public void postRegisterNameTaken() {
		LoginCredentialsPayload payload = new LoginCredentialsPayload(UserName, User2Password);

		Response response = resource.postRegister(payload);

		Assert.assertEquals(Response.Status.CONFLICT, response.getStatusInfo());
		NewCookie cookie = response.getCookies().get(RequireUserAccountFilter.CookieName);
		Assert.assertNull(cookie);

		Assert.assertEquals(NumberOfAccounts, (long)userAccountStore.getCount());
	}

	@Test
	public void postDeleteOk() {
		resource.accountProxy = generateAccountProxy();

		Response response = resource.postDelete();

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		Assert.assertEquals(NumberOfAccounts - 1, (long)userAccountStore.getCount());
	}

	@Test
	public void postChangePasswordOk() {
		resource.accountProxy = generateAccountProxy();

		ChangePasswordRequestPayload payload = new ChangePasswordRequestPayload(UserPassword, User2Password);

		Response response = resource.postChangePassword(payload);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		Assert.assertEquals(NumberOfAccounts, (long)userAccountStore.getCount());
		UserAccount modifiedUserAccount = userAccountStore.get(userAccount.getId());
		Assert.assertNotNull(modifiedUserAccount);
		Assert.assertEquals(userAccount.getName(), modifiedUserAccount.getName());
		Assert.assertFalse(modifiedUserAccount.verifyPassword(UserPassword));
		Assert.assertTrue(modifiedUserAccount.verifyPassword(User2Password));
	}

	@Test
	public void postChangePasswordWrongOldPassword() {
		resource.accountProxy = generateAccountProxy();

		ChangePasswordRequestPayload payload = new ChangePasswordRequestPayload(WrongPassword, User2Password);

		Response response = resource.postChangePassword(payload);

		Assert.assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());

		Assert.assertEquals(NumberOfAccounts, (long)userAccountStore.getCount());
		UserAccount modifiedUserAccount = userAccountStore.get(userAccount.getId());
		Assert.assertNotNull(modifiedUserAccount);
		Assert.assertEquals(userAccount.getName(), modifiedUserAccount.getName());
		Assert.assertTrue(modifiedUserAccount.verifyPassword(UserPassword));
		Assert.assertFalse(modifiedUserAccount.verifyPassword(User2Password));
	}
}