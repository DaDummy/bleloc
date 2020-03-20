package edu.kit.informatik.pse.bleloc.client.model.user;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Base64;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;


@RunWith(AndroidJUnit4.class)
public class UserDataTest {

	private static UserData userData;
	private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	private static SharedPreferences.Editor editor;

	@Before
	public void setUp() {
		editor = preferences.edit();
		editor.putString("name", "UserTest");
		editor.putString("cookie", "UserCookie");
		editor.putString("localKey", "password");
		editor.commit();
		userData = new UserData(preferences);
	}

	@Test
	public void initialTest() {
		Assert.assertNotNull(userData);
	}

	@Test
	public void getUserNameTest() {
		Assert.assertEquals(userData.getName(), "UserTest");
	}

	@Test
	public void setCookieTest() {
		userData.setCookie("NewUserCookie");
		Assert.assertTrue(userData.getCookie().contains("NewUserCookie"));
	}

	@Test
	public void getCookieTest() {
		Assert.assertEquals(userData.getCookie(), "UserCookie");
	}

	@Test
	public void getLocalKeyTest() throws Exception {
		byte[] array = "password".getBytes("ISO-8859-1");
		Assert.assertEquals(Base64.getEncoder().encodeToString(userData.getLocalKey()),
		                    Base64.getEncoder().encodeToString(array));
	}

	@Test
	public void setLocalKeyTest() {
		byte[] newLocalkey = {80, 60, 90, 10, 10, 20};
		userData.setLocalKey(newLocalkey);
		Assert.assertEquals(Base64.getEncoder().encodeToString(newLocalkey), Base64.getEncoder().encodeToString(userData.getLocalKey()));
	}

	@Test
	public void getHashedPasswordTest() {
		String result = "be6c3fc230b32507b23f61fe7fbf28c1";
		Assert.assertEquals(userData.getHashedPassword(), result);
	}

	@Test
	public void getLocalKeyFromRawTest() {
		String pw = "password";
		String result = "x2+y5+EPTw77LnqTp3rgUA==";
		Assert.assertEquals(result, Base64.getEncoder().encodeToString(userData.getLocalKeyFromRaw(pw)));
	}

	@Test
	public void getHashedPasswordFromRawTest() {
		String pw = "password";
		String result = "c541bfd588ffd665c5ca4ca192de944b";
		Assert.assertEquals(result, userData.getHashedPasswordFromRaw(pw));
	}

}

