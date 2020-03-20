package edu.kit.informatik.pse.bleloc.client.model.user;

/**
 * Listener to wait for the result of a registration request.
 */
public interface RegistrationEventListener {
	/**
	 * Called by a network thread when receiving the authentication result.
	 *
	 * @param result
	 * 		The result of the authentication request.
	 */
	public void onReceiveRegistrationResult(AuthenticationResult result);
}
