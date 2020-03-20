package edu.kit.informatik.pse.bleloc.model;

/**
 * Interface for objects that can authenticate users.
 */
public interface Authenticator {

	/**
	 * Creates a cookie
	 */
	public String createCookie(Long accountId);

	/**
	 * Verifies a cookie
	 */
	public Long verifyCookie(String cookie);
}
