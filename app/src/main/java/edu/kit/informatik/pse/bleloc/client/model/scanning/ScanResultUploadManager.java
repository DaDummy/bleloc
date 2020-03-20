package edu.kit.informatik.pse.bleloc.client.model.scanning;

import android.util.Log;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ScanResultUploadResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RequestManager;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.ScanResultUploadRequest;
import edu.kit.informatik.pse.bleloc.payload.DeviceTrackingResultPayload;

import java.util.Date;

/**
 * Manages the upload of scan results to the server.
 */
public class ScanResultUploadManager implements ScanResultUploadResultListener {
	private ScanResultToUploadStore scanResultToUploadStore;
	private RequestManager requestManager;
	private ScanResultToUpload pending;

	public ScanResultUploadManager(ScanResultToUploadStore scanResultToUploadStore, RequestManager requestManager) {
		this.scanResultToUploadStore = scanResultToUploadStore;
		this.requestManager = requestManager;
	}

	/**
	 * Takes the local scan result store and uploads its data.<br> The data is not removed the store, until successfully
	 * uploaded.
	 */
	public void triggerUpload() {
		if (pending != null) {
			Log.i("ScanResultUploadManager", "another request pending");
			return;
		}

		pending = scanResultToUploadStore.getOldest();
		if (pending == null) {
			Log.i("ScanResultUploadManager", "no more results to upload");
			return;
		}

		Log.i("ScanResultUploadManager", "Uploading a result");
		DeviceTrackingResultPayload payload = new DeviceTrackingResultPayload();
		payload.setEncryptedData(pending.getEncryptedLocationData());
		payload.setHashedHardwareIdentifier(pending.getHashedHardwareIdentifier().toString());
		payload.setEncounteredAt(new Date());
		//TODO: what about setEncounteredAt and setTrackingResultId?
		ScanResultUploadRequest request = new ScanResultUploadRequest(payload);
		request.registerListener(this);
		requestManager.send(request);
	}

	@Override
	public void onReceiveScanResultUploadResult() {
		Log.i("ScanResultUploadManager", "Uploaded a result");
		scanResultToUploadStore.delete(pending);
		pending = null;
		triggerUpload();
	}
}
