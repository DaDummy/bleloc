package edu.kit.informatik.pse.bleloc.client.model.user;

/**
 * Listener for logout request results.
 */
public interface LogoutEventListener {
	/**
	 * Called by a network thread when receiving the result for a logout request.
	 */
	public void onReceiveLogoutResult(AuthenticationResult result);
}
