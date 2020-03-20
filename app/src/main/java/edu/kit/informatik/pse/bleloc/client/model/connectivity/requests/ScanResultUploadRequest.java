package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ScanResultUploadResultListener;
import edu.kit.informatik.pse.bleloc.payload.DeviceTrackingResultPayload;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ScanResultUploadRequest extends BackendRequest<ScanResultUploadResultListener> {
	private DeviceTrackingResultPayload payload;
	private boolean success;

	public ScanResultUploadRequest(DeviceTrackingResultPayload payload) {
		this.payload = payload;
	}

	@Override
	protected void handle() throws IOException {
		Log.i("ScanResultUploadRequest", "handle");
		HttpURLConnection connection = connect("POST", "/scan/result");
		connection.addRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		new ObjectMapper().writeValue(connection.getOutputStream(), payload);

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			success = true;
		}

		connection.disconnect();
	}

	@Override
	protected void result() {
		if (success) {
			for (ScanResultUploadResultListener l : listenerSet) {
				l.onReceiveScanResultUploadResult();
			}
		}
	}
}
