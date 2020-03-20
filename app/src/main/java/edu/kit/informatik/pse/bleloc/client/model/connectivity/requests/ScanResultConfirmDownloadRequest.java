package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ScanResultConfirmDownloadResultListener;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ScanResultConfirmDownloadRequest extends BackendRequest<ScanResultConfirmDownloadResultListener> {

	private long scanResultId;
	private boolean success;

	public ScanResultConfirmDownloadRequest(long scanResultId) {
		this.scanResultId = scanResultId;
	}

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("POST", "/scan/resultReceived/" + scanResultId);
		connection.setDoOutput(true);
		success = (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
		connection.disconnect();
	}

	@Override
	protected void result() {
		if (!success) {
			return;
		}

		for (ScanResultConfirmDownloadResultListener l : listenerSet) {
			l.onReceiveScanResultConfirmDownloadResult();
		}
	}
}
