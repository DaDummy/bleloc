package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.DeleteAccountResultListener;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DeleteAccountRequest extends BackendRequest<DeleteAccountResultListener> {

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("POST", "/user/delete");
		connection.setDoOutput(true);
		connection.getResponseCode();
		connection.disconnect();
	}

	@Override
	protected void result() {
		for (DeleteAccountResultListener l : listenerSet) {
			l.onReceiveDeleteAccountResult();
		}
	}
}
