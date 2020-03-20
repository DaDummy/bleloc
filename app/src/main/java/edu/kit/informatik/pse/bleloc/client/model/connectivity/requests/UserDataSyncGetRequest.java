package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.UserDataSyncGetResultListener;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UserDataSyncGetRequest extends BackendRequest<UserDataSyncGetResultListener> {
	private static final String RequestMethod = "GET";
	private static final String RequestPathPrefix = "/user/sync/data/";

	private final long syncId;
	private final SynchronizableObject localObject;

	private UserDataPayload payload;

	public UserDataSyncGetRequest(long syncId) {
		this(syncId, null);
	}

	public UserDataSyncGetRequest(long syncId, SynchronizableObject localObject) {
		this.syncId = syncId;
		this.localObject = localObject;
	}

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect(RequestMethod, RequestPathPrefix + syncId);
		connection.addRequestProperty("Content-Type", "application/json");
		ObjectMapper objectMapper = new ObjectMapper();

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
		for (UserDataSyncGetResultListener l : listenerSet) {
			l.onReceiveUserDataSyncGetResult(payload, localObject);
		}
	}
}
