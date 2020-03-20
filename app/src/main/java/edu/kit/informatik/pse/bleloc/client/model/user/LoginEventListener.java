package edu.kit.informatik.pse.bleloc.client.model.user;

/**
 * Listener for login request results.
 */
public interface LoginEventListener {
	/**
	 * Called by a network thread when receiving the result for a login request.
	 */
	public void onReceiveLoginResult(AuthenticationResult result);
}
