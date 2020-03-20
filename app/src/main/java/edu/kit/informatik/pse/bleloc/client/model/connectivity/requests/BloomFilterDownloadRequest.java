package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.BloomFilterDownloadResultListener;
import edu.kit.informatik.pse.bleloc.model.DeviceHashTable;
import edu.kit.informatik.pse.bleloc.payload.DeviceHashTablePayload;

import java.io.IOException;
import java.net.HttpURLConnection;

public class BloomFilterDownloadRequest extends BackendRequest<BloomFilterDownloadResultListener> {
	private DeviceHashTable result;

	@Override
	protected void handle() throws IOException {
		HttpURLConnection connection = connect("GET", "/scan/filter");
		DeviceHashTablePayload payload = new ObjectMapper().readValue(connection.getInputStream(), DeviceHashTablePayload.class);
		connection.disconnect();
		result = new DeviceHashTable(payload.getModifiedAt(), payload.getData());
	}

	@Override
	protected void result() {
		for (BloomFilterDownloadResultListener l : listenerSet) {
			l.onReceiveBloomFilterDownloadResult(result);
		}
	}
}
