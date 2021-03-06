package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.UserDataSyncAddResultListener;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UserDataSyncAddRequest extends BackendRequest<UserDataSyncAddResultListener> {
	private static final String RequestMethod = "POST";
	private static final String RequestPath = "/user/sync/addData";

	private final SynchronizableObject localObject;

	private UserDataPayload payload;

	public UserDataSyncAddRequest(UserDataPayload payload, SynchronizableObject localObject) {
		this.payload = payload;
		this.localObject = localObject;
	}

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect(RequestMethod, RequestPath);
		connection.addRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(connection.getOutputStream(), payload);

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			payload = objectMapper.readValue(connection.getInputStream(), UserDataPayload.class);
		} else {
			payload = null;
			// TODO(ca): Handle error
		}

		connection.disconnect();
	}

	@Override
	protected void result() {
		for (UserDataSyncAddResultListener l : listenerSet) {
			l.onReceiveUserDataSyncAddResult(payload, localObject);
		}
	}
}
