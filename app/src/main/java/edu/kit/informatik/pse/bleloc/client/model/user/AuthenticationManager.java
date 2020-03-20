package edu.kit.informatik.pse.bleloc.client.model.user;

import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ChangePasswordResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.DeleteAccountResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.LoginResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.RegisterUserResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.*;
import edu.kit.informatik.pse.bleloc.payload.ChangePasswordRequestPayload;
import edu.kit.informatik.pse.bleloc.payload.LoginCredentialsPayload;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages a users registration, login and logout processes.
 */
public class AuthenticationManager
	implements RegisterUserResultListener, LoginResultListener, DeleteAccountResultListener {
	private RequestManager requestManager;
	private UserData userData;
	private Set<LoginEventListener> loginEventListenerSet = new HashSet<>();
	private Set<RegistrationEventListener> registrationEventListenerSet = new HashSet<>();
	private Set<LogoutEventListener> logoutEventListenerSet = new HashSet<>();

	public AuthenticationManager(UserData userData) {
		this.userData = userData;
	}

	/**
	 * Tries to register an account on the server with username and password.
	 *
	 * @param username
	 * 		the user identifier to register
	 * @param password
	 * 		the user password
	 * @return an User object with its verifivation data contained
	 */
	public void register(String username, String password) {
		RegisterUserRequest registerRequest =
			new RegisterUserRequest(new LoginCredentialsPayload(username, UserData.getHashedPasswordFromRaw(password)));
		registerRequest.registerListener(this);
		requestManager.send(registerRequest);
	}

	/**
	 * Tries to log in on the server, for an existing account.
	 *
	 * @param username
	 * 		the User to log in
	 */
	public void login(String username, String password) {
		userData.setLocalKey(UserData.getLocalKeyFromRaw(password));
		LoginRequest loginRequest =
			new LoginRequest(new LoginCredentialsPayload(username, userData.getHashedPassword()));
		loginRequest.registerListener(this);
		requestManager.send(loginRequest);
	}

	/**
	 * Tries to log the user out from the server.
	 */
	public void logout() {
		userData.setCookie(null);
		onReceiveDeleteAccountResult();
	}

	/**
	 * Deletes the user from the server and invalidates all related tokens. Deleted data includes the user-name as well
	 * as all cloud data associated with that user.
	 *
	 * @return <code>true</code> for successful deletion, <code>false</code> otherwise
	 */
	public void delete() {
		DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
		deleteAccountRequest.registerListener(this);
		requestManager.send(deleteAccountRequest);
	}

	public void changePassword(String newPassword) {
		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(new ChangePasswordRequestPayload(userData.getHashedPassword(), UserData.getHashedPasswordFromRaw(newPassword)));
		changePasswordRequest.registerListener(new ChangePasswordResultListener() {
			@Override
			public void onPasswordChanged() {
				userData.setLocalKey(UserData.getLocalKeyFromRaw(newPassword));
				// TODO: reencrypt?
			}
		});
		requestManager.send(changePasswordRequest);
	}

	public boolean isLoggedIn() {
		return userData.getCookie() != null;
	}

	/**
	 * Registers a listener for lgoin request results. The listener is called when a login request result from the
	 * server is received.<br>
	 * <code>null</code> input is ignored
	 *
	 * @param listener
	 * 		The listener to register
	 */
	public void registerListener(LoginEventListener listener) {
		loginEventListenerSet.add(listener);
	}

	/**
	 * Registers a listener for registration request results. The listener is called when a registration request result
	 * from the server is received.
	 * <code>null</code> input is ignored
	 *
	 * @param listener
	 * 		The listener to register
	 */
	public void registerListener(RegistrationEventListener listener) {
		registrationEventListenerSet.add(listener);
	}

	/**
	 * Registers a listener for logout request results. The listener is called when a logout request result from the
	 * server is received.
	 * <code>null</code> input is ignored
	 *
	 * @param listener
	 * 		The listener to register
	 */
	public void registerListener(LogoutEventListener listener) {
		logoutEventListenerSet.add(listener);
	}

	/**
	 * Deregisters the listener. A deregistered listener is no longer being called on corresponding events.<br>
	 * <p>
	 * Only registered listeners can be deregistered.<br> The direct object reference is requiered.
	 * </p>
	 *
	 * @param listener
	 * 		The listener to deregister
	 */
	public void deregisterListener(LoginEventListener listener) {
		loginEventListenerSet.remove(listener);
	}

	/**
	 * Deregisters the listener. A deregistered listener is no longer being called on corresponding events.<br>
	 * <p>
	 * Only registered listeners can be deregistered.<br> The direct object reference is requiered.
	 * </p>
	 *
	 * @param listener
	 * 		The listener to deregister
	 */
	public void deregisterListener(RegistrationEventListener listener) {
		registrationEventListenerSet.remove(listener);
	}

	/**
	 * Deregisters the listener. A deregistered listener is no longer being called on corresponding events.<br>
	 * <p>
	 * Only registered listeners can be deregistered.<br> The direct object reference is requiered.
	 * </p>
	 *
	 * @param listener
	 * 		The listener to deregister
	 */
	public void deregisterListener(LogoutEventListener listener) {
		logoutEventListenerSet.remove(listener);
	}

	@Override
	public void onReceiveRegisterUserResult() {
		for (RegistrationEventListener l : registrationEventListenerSet) {
			l.onReceiveRegistrationResult(AuthenticationResult.SUCCESS);
		}
	}

	@Override
	public void onReceiveLoginResult(String cookie) {
		userData.setCookie(cookie);

		AuthenticationResult result = AuthenticationResult.INVALID_REQUEST;
		if (cookie != null) {
			result = AuthenticationResult.SUCCESS;
		}

		for (LoginEventListener l : loginEventListenerSet) {
			l.onReceiveLoginResult(result);
		}
	}

	@Override
	public void onReceiveDeleteAccountResult() {
		userData.setCookie(null);
		for (LogoutEventListener l : logoutEventListenerSet) {
			l.onReceiveLogoutResult(AuthenticationResult.SUCCESS);
		}
	}

	/**
	 * Sets the request manager to use for network interaction.
	 *
	 * @param requestManager
	 * 		The request manager to set
	 */
	public void setRequestManager(RequestManager requestManager) {
		this.requestManager = requestManager;
	}
}
