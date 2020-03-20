package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.RegisterTrackedDeviceResultListener;
import edu.kit.informatik.pse.bleloc.payload.Device;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RegisterTrackedDeviceRequest extends BackendRequest<RegisterTrackedDeviceResultListener> {

	private Device payload;

	public RegisterTrackedDeviceRequest(Device payload) {
		this.payload = payload;
	}

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("POST", "/device/register");
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
		for (RegisterTrackedDeviceResultListener l : listenerSet) {
			l.onReceiveRegisterTrackedDeviceResult();
		}
	}

}
