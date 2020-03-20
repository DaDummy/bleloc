package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.UserDataSyncIndexResultListener;
import edu.kit.informatik.pse.bleloc.payload.UserDataIndexPayload;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UserDataSyncIndexRequest extends BackendRequest<UserDataSyncIndexResultListener> {
	private static final String RequestMethod = "GET";
	private static final String RequestPath = "/user/sync/data";

	private UserDataIndexPayload payload;

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect(RequestMethod, RequestPath);
		connection.addRequestProperty("Content-Type", "application/json");
		ObjectMapper objectMapper = new ObjectMapper();

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			payload = objectMapper.readValue(connection.getInputStream(), UserDataIndexPayload.class);
		} else {
			payload = null;
			// TODO(ca): Handle error
		}

		connection.disconnect();
	}

	@Override
	protected void result() {
		for (UserDataSyncIndexResultListener l : listenerSet) {
			l.onReceiveUserDataSyncIndex(payload);
		}
	}
}
