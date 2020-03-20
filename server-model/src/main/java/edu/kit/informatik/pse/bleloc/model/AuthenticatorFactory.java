package edu.kit.informatik.pse.bleloc.model;

public interface AuthenticatorFactory {
	/**
	 * Creates an Authenticator.
	 *
	 * @return the Authenticator
	 */
	public Authenticator createAuthenticator(AuthenticatorPurpose purpose, Long invalidAccountId);
}