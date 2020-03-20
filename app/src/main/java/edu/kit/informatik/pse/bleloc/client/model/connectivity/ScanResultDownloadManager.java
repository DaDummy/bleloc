package edu.kit.informatik.pse.bleloc.client.model.connectivity;

import android.util.Log;
import edu.kit.informatik.pse.bleloc.client.controller.AppDatabase;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ScanResultDownloadResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ScanResultConfirmDownloadResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.UserDataSyncEventListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RequestManager;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.ScanResultConfirmDownloadRequest;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.ScanResultDownloadRequest;
import edu.kit.informatik.pse.bleloc.client.model.device.Device;
import edu.kit.informatik.pse.bleloc.client.model.device.Location;
import edu.kit.informatik.pse.bleloc.client.model.utils.Encryptor;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import edu.kit.informatik.pse.bleloc.payload.DeviceTrackingResultPayload;
import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Manages the download of scan results for tracked devices.
 */
public class ScanResultDownloadManager
		implements ScanResultDownloadResultListener, ScanResultConfirmDownloadResultListener,
		           UserDataSyncEventListener {

	private AppDatabase database;
	private RequestManager requestManager;
	private UserDataSyncManager syncManager;
	private boolean isActive = false;
	private boolean isPendingCancel = false;
	private boolean hasReceivedNewData = false;

	public ScanResultDownloadManager(AppDatabase database, RequestManager requestManager, UserDataSyncManager syncManager) {
		this.database = database;
		this.requestManager = requestManager;
		this.syncManager = syncManager;
	}

	/**
	 * Downloads scan results from other devices, that have been uploaded to the server.
	 */
	public void triggerDownload() {
		if (!isActive()) {
			isActive = true;

			downloadNextResult();
		} else {
			Log.e("ScanResultDownload","Triggered scan result download, but scan result download is already active!");
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void cancel() {
		if (isActive()) {
			isPendingCancel = true;
		}
	}

	public boolean isPendingCancel() {
		return isPendingCancel;
	}

	@Override
	public void onPreSync() {
		//
	}

	@Override
	public void onPostSync() {
		if (!isActive()) {
			triggerDownload();
		}
	}

	@Override
	public void onReceiveScanResultDownloadResult(DeviceTrackingResultPayload result) {
		// If there are no more results, cancel was requested or an error occurred, simply stop downloading
		if (result == null || isPendingCancel()) {
			isPendingCancel = false;
			isActive = false;

			if (hasReceivedNewData) {
				syncManager.triggerSync();
				hasReceivedNewData = false;
			}

			return;
		}

		for (Device d : database.getDeviceStore().getDevices()) {
			Log.i("ScanResultDownloadManag", "Checking: " + d.getHashedHardwareIdentifier().toString() + " - " + result.getHashedHardwareIdentifier());
			if (d.getHashedHardwareIdentifier().equals(HashedMacAddress.fromString(result.getHashedHardwareIdentifier()))) {
				try {
					Location location = Encryptor.decryptScanResult(d.getHardwareIdentifier(), result.getEncryptedData());
					location.setDeviceId(d.getId());
					database.getLocationStore().add(location);
					hasReceivedNewData = true;
					Log.i("ScanResultDownloadManag", "added location");
				} catch (InvalidCipherTextException e) {
					Log.i("ScanResultDownloadManag", "silently ignoring InvalidCipherTextException");
				}
			}
		}

		ScanResultConfirmDownloadRequest request = new ScanResultConfirmDownloadRequest(result.getTrackingResultId());
		request.registerListener(this);
		requestManager.send(request);
	}

	@Override
	public void onReceiveScanResultConfirmDownloadResult() {
		downloadNextResult();
	}

	private void downloadNextResult() {
		ScanResultDownloadRequest request = new ScanResultDownloadRequest();
		request.registerListener(this);
		requestManager.send(request);
	}
}
