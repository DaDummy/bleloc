package edu.kit.informatik.pse.bleloc.client.model.user;

import android.content.SharedPreferences;
import edu.kit.informatik.pse.bleloc.client.model.utils.Encryptor;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Observable;

/**
 * This class represents the user data.
 */
public class UserData {

	private SharedPreferences preferences;

	/**
	 * foo bar
	 *
	 * @param preferences
	 * 		The Android shared preferences object.
	 */
	public UserData(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	/**
	 * Returns the user-name.
	 *
	 * @return The user-name.
	 */
	public String getName() {
		return preferences.getString("name", null);
	}

	/**
	 * Sets a new session cookie.
	 *
	 * @param cookie
	 * 		The new session cookie.
	 */
	public void setCookie(String cookie) {
		preferences.edit().putString("cookie", cookie).apply();
	}

	/**
	 * Returns the session cookie.
	 *
	 * @return The current session cookie.
	 */
	public String getCookie() {
		return preferences.getString("cookie", null);
	}

	public byte[] getLocalKey() {
		try {
			return preferences.getString("localKey", null).getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void setLocalKey(byte[] key) {
		try {
			preferences.edit().putString("localKey", new String(key, "ISO-8859-1")).apply();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String getHashedPassword() {
		return Encryptor.hashedMacAddressFromKey(getLocalKey()).toString();
	}

	public static byte[] getLocalKeyFromRaw(String pw) {
		try {
			return Encryptor.keyFromAddress(pw.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getHashedPasswordFromRaw(String pw) {
		return Encryptor.hashedMacAddressFromKey(getLocalKeyFromRaw(pw)).toString();
	}
}
