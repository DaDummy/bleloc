package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ListTrackedDevicesResultListener;
import edu.kit.informatik.pse.bleloc.payload.DeviceHashTablePayload;
import edu.kit.informatik.pse.bleloc.payload.DeviceIndexPayload;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class ListTrackedDevicesRequest extends BackendRequest<ListTrackedDevicesResultListener> {
	private DeviceIndexPayload payload;

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("GET", "/device/list");
		payload = new ObjectMapper().readValue(connection.getInputStream(), DeviceIndexPayload.class);
		connection.disconnect();
	}

	@Override
	protected void result() {
		for (ListTrackedDevicesResultListener l : listenerSet) {
			l.onReceiveListTrackedDevicesResult(payload);
		}
	}
}
