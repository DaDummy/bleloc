package edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners;

import edu.kit.informatik.pse.bleloc.payload.DeviceIndexPayload;

import java.util.EventListener;

public interface ListTrackedDevicesResultListener extends EventListener {

	public void onReceiveListTrackedDevicesResult(DeviceIndexPayload deviceIndexPayload);
}
