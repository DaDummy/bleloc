package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.LoginResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.RegisterUserResultListener;
import edu.kit.informatik.pse.bleloc.payload.LoginCredentialsPayload;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

public class RegisterUserRequest extends BackendRequest<RegisterUserResultListener> {
	private LoginCredentialsPayload payload;

	public RegisterUserRequest(LoginCredentialsPayload payload) {
		this.payload = payload;
	}

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("POST", "/user/register");
		connection.addRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		new ObjectMapper().writeValue(connection.getOutputStream(), payload);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			//
		} else {
			//
		}
		connection.disconnect();
	}

	@Override
	protected void result() {
		for (RegisterUserResultListener l : listenerSet) {
			l.onReceiveRegisterUserResult();
		}
	}
}
