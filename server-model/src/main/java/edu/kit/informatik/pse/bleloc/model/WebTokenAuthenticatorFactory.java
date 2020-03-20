package edu.kit.informatik.pse.bleloc.model;

public class WebTokenAuthenticatorFactory implements AuthenticatorFactory {

	@Override
	public WebTokenAuthenticator createAuthenticator(AuthenticatorPurpose purpose, Long invalidAccountId) {
		return new WebTokenAuthenticator(purpose, invalidAccountId);
	}
}
