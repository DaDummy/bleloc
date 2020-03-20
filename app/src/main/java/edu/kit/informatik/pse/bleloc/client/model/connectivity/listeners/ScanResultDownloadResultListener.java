package edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners;

import edu.kit.informatik.pse.bleloc.payload.DeviceTrackingResultPayload;

import java.util.EventListener;

public interface ScanResultDownloadResultListener extends EventListener {

	public void onReceiveScanResultDownloadResult(DeviceTrackingResultPayload deviceTrackingResultPayload);
}
