package edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners;

import edu.kit.informatik.pse.bleloc.model.DeviceHashTable;

import java.util.EventListener;

public interface BloomFilterDownloadResultListener extends EventListener {

	public void onReceiveBloomFilterDownloadResult(DeviceHashTable deviceHashTable);
}
