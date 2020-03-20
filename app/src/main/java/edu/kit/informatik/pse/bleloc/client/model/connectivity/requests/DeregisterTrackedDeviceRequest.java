package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.DeregisterTrackedDeviceResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.RegisterTrackedDeviceResultListener;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DeregisterTrackedDeviceRequest extends BackendRequest<DeregisterTrackedDeviceResultListener> {

	private String deviceId;

	public DeregisterTrackedDeviceRequest(HashedMacAddress deviceId) {
		this.deviceId = deviceId.toString();
	}

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("DELETE", "/device/registry/" + deviceId);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			//
		} else {
			//
		}
		connection.disconnect();
	}

	@Override
	protected void result() {
		for (DeregisterTrackedDeviceResultListener l : listenerSet) {
			l.onReceiveDeregisterTrackedDeviceResult();
		}
	}

}
