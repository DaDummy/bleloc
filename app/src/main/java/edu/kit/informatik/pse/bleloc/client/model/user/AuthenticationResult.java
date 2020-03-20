package edu.kit.informatik.pse.bleloc.client.model.user;

/**
 * Status result enum for authentication requests to the server.
 */
public enum AuthenticationResult {
	/**
	 * Authentication succeeded without any issues
	 */
	SUCCESS, /**
	 * The request failed due to invalid values
	 */
	INVALID_REQUEST, /**
	 * The server did not respond on the request before timeout
	 */
	SERVER_BUSY, /**
	 * No network connection available
	 */
	NETWORK_ERROR, /**
	 * A user with the selected name is already registered
	 */
	USERNAME_TAKEN;
}
