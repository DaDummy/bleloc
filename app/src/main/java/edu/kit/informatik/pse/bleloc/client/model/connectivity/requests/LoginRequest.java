package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.LoginResultListener;
import edu.kit.informatik.pse.bleloc.payload.LoginCredentialsPayload;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

public class LoginRequest extends BackendRequest<LoginResultListener> {
	private LoginCredentialsPayload payload;
	private String token;

	public LoginRequest(LoginCredentialsPayload payload) {
		this.payload = payload;
	}

	@Override
	protected void handle() throws IOException {
		// TODO(ca): Magic values all over the place - please use static final fields and maybe try to collect repeating values in a common location
		// TODO(ca): The following lines seem to have some code that will be repeated in other requests - please consider extracting them into a method in a common location
		HttpURLConnection connection = connect("POST", "/user/login");
		connection.addRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		new ObjectMapper().writeValue(connection.getOutputStream(), payload);
		// TODO(ca): Why is the following line here? It seems to work just fine without.
		// TODO(ca): Documentation says getInputStream throws an exception if the connection failed - how is this exception handled and passed on to the AuthenticationManager/LoginActivity?
		// Commented the following line, since it caused result() to never be called which made failed logins look like they simply took forever
		//connection.getInputStream().skip(100000);

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			String headerField = connection.getHeaderField("Set-Cookie");
			if (headerField != null) {
				for (HttpCookie cookie : HttpCookie.parse(headerField)) {
					if (cookie.getName().equals("userAuth")) {
						token = cookie.getValue();
					}
				}
			}
		} else {
			// TODO(ca): We really should communicate errors to the user
		}

		// TODO(ca): The disconnect call was missing - double check if it is still missing in other requests
		connection.disconnect();
	}

	@Override
	protected void result() {
		for (LoginResultListener l : listenerSet) {
			l.onReceiveLoginResult(token);
		}
	}
}
