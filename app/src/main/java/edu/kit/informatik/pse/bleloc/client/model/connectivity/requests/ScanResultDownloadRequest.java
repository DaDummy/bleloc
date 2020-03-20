package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ScanResultDownloadResultListener;
import edu.kit.informatik.pse.bleloc.payload.DeviceTrackingResultPayload;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ScanResultDownloadRequest extends BackendRequest<ScanResultDownloadResultListener> {
	private DeviceTrackingResultPayload result;

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("GET", "/scan/result/0");
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			result = new ObjectMapper().readValue(connection.getInputStream(), DeviceTrackingResultPayload.class);
		}
		connection.disconnect();
	}

	@Override
	protected void result() {
		for (ScanResultDownloadResultListener l : listenerSet) {
			l.onReceiveScanResultDownloadResult(result);
		}
	}
}
