package edu.kit.informatik.pse.bleloc.client.model.user;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

@RunWith(AndroidJUnit4.class)
public class AuthenticationManagerTest {
	private static AuthenticationManager authenticationManager;
	private static SharedPreferences preferences;
	private  UserData userData;
	private RequestManager requestManager;
	private byte[] array = {1,2,3,4,5,6};

	@Mock
	RequestManager requestManagerTest;
	@Mock
	LoginEventListener loginEventListener;
	@Mock
	RegistrationEventListener registrationEventListener;
	@Mock
	LogoutEventListener logoutEventListener;


	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		preferences.edit().putString("name", "User").apply();
		preferences.edit().putString("cookie", "UserCookie").apply();
		preferences.edit().putString("localKey", "password").apply();
		userData = new UserData(preferences);
		authenticationManager = new AuthenticationManager(userData);
		requestManager = new RequestManager(userData);
		authenticationManager.setRequestManager(requestManager);
	}

	@Test
	public void initialization(){
		Assert.assertNotNull(authenticationManager);
	}

	@Test
	public void registerTest(){
		authenticationManager.register("USER", "PWD");
	}

	@Test
	public void loginTest(){
		authenticationManager.login("User", "password");
		userData.setCookie("Cookie");
		Assert.assertTrue(authenticationManager.isLoggedIn());
	}

	@Test
	public void logoutTest(){
		authenticationManager.logout();
		Assert.assertFalse(authenticationManager.isLoggedIn());
	}

	@Test
	public void deleteTest() {
		authenticationManager.delete();
	}

	@Test
	public void changePasswordTest() {
		authenticationManager.login("User", "password");
		authenticationManager.changePassword("newPassword");
	}

	@Test
	public void isLoggedInTest() {
		authenticationManager.login("User", "password");
		userData.setCookie("Cookie");
		Assert.assertTrue(authenticationManager.isLoggedIn());
	}

	@Test
	public void setRequestManagerTest() {
		authenticationManager.setRequestManager(requestManagerTest);
	}

	@Test
	public void registerListenerTest() {
		authenticationManager.registerListener(loginEventListener);
		authenticationManager.registerListener(registrationEventListener);
		authenticationManager.registerListener(logoutEventListener);
	}

	@Test
	public void deregisterListenerTest() {
		authenticationManager.deregisterListener(loginEventListener);
		authenticationManager.deregisterListener(registrationEventListener);
		authenticationManager.deregisterListener(logoutEventListener);
	}

	@Test
	public void onReceiveRegisterUserResultTest()
	{
		authenticationManager.registerListener(registrationEventListener);
		authenticationManager.onReceiveRegisterUserResult();
		authenticationManager.deregisterListener(loginEventListener);
	}

	@Test
	public void onReceiveLoginResultTest() {
		authenticationManager.registerListener(loginEventListener);
		authenticationManager.onReceiveLoginResult("cookie");
		authenticationManager.deregisterListener(loginEventListener);
	}

	@Test
	public void onReceiveDeleteAccountResultTest(){
		authenticationManager.registerListener(logoutEventListener);
		authenticationManager.onReceiveDeleteAccountResult();
		authenticationManager.deregisterListener(loginEventListener);
	}
}
