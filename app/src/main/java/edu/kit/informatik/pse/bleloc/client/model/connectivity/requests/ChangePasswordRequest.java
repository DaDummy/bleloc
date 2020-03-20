package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ChangePasswordResultListener;
import edu.kit.informatik.pse.bleloc.payload.ChangePasswordRequestPayload;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ChangePasswordRequest extends BackendRequest<ChangePasswordResultListener> {

	private ChangePasswordRequestPayload payload;

	public ChangePasswordRequest(ChangePasswordRequestPayload payload) {
		this.payload = payload;
	}

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("POST", "/user/changePassword");
		connection.addRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		new ObjectMapper().writeValue(connection.getOutputStream(), payload);
		connection.getInputStream().skip(100000);
	}

	@Override
	protected void result() {
		for (ChangePasswordResultListener l : listenerSet) {
			l.onPasswordChanged();
		}
	}
}
