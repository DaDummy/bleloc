package edu.kit.informatik.pse.bleloc.client.model.connectivity;

import android.util.Log;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.DeregisterTrackedDeviceRequest;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.ListTrackedDevicesRequest;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RegisterTrackedDeviceRequest;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RequestManager;
import edu.kit.informatik.pse.bleloc.client.model.device.*;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.UserDataSyncEventListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.RegisterTrackedDeviceResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.DeregisterTrackedDeviceResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.ListTrackedDevicesResultListener;
import edu.kit.informatik.pse.bleloc.payload.DeviceIndexPayload;

/**
 * Manages the tracking states of devices and synchronizes them with the server.
 */
public class TrackingStateManager implements UserDataSyncEventListener, RegisterTrackedDeviceResultListener,
                                             DeregisterTrackedDeviceResultListener, ListTrackedDevicesResultListener {

	private DeviceTrackingStateStore store;
	private DeviceStore deviceStore;
	private RequestManager requestManager;
	private boolean isActive = false;
	private boolean isPendingCancel = false;

	/**
	 * Initialize TrackingStateManager with the DeviceTrackingStateStore to manage.
	 *
	 * @param store
	 * 		The store to manage
	 */
	public TrackingStateManager(DeviceTrackingStateStore store, DeviceStore deviceStore, RequestManager requestManager) {
		this.store = store;
		this.deviceStore = deviceStore;
		this.requestManager = requestManager;
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

	/**
	 * Synchronizes the tracking states with the server.
	 */
	public void refreshTrackingState() {
		if (!isActive()) {
			isActive = true;

			ListTrackedDevicesRequest request = new ListTrackedDevicesRequest();
			request.registerListener(this);
			requestManager.send(request);
		} else {
			Log.e("TrackingStateManager","Refresh triggered, but refresh is already active!");
		}
	}

	/**
	 * Activate or deactivate tracking for a device.
	 *
	 * @param device
	 * 		The device to set the tracking state for
	 * @param state
	 * 		<code>true</code>: set tracking, <code>false</code>: disable tracking
	 */
	public void toggleDeviceTrackingStatus(Device device, boolean state) {
		if (state && !isTracked(device)) {
			Log.i("TrackingStateManager", "Toggle on");
			RegisterTrackedDeviceRequest request = new RegisterTrackedDeviceRequest(new edu.kit.informatik.pse.bleloc.payload.Device(device.getHashedHardwareIdentifier()));
			request.registerListener(this);
			requestManager.send(request);
		}

		if (!state && isTracked(device)) {
			Log.i("TrackingStateManager", "Toggle off");
			DeregisterTrackedDeviceRequest request = new DeregisterTrackedDeviceRequest(device.getHashedHardwareIdentifier());
			request.registerListener(this);
			requestManager.send(request);
		}
	}

	/**
	 * Activate or deactivate tracking for a device.
	 *
	 * @param device
	 * 		The device to set the tracking state for
	 */
	public boolean isTracked(Device device) {
		DeviceTrackingState state = store.getByDevice(device.getId());
		return state != null && state.getTrackingState() == TrackingState.TRACKED;
	}

	@Override
	public void onPreSync() {
		//
	}

	@Override
	public void onPostSync() {
		refreshTrackingState();
	}

	@Override
	public void onReceiveRegisterTrackedDeviceResult() {
		refreshTrackingState();
	}

	@Override
	public void onReceiveDeregisterTrackedDeviceResult() {
		refreshTrackingState();
	}

	@Override
	public void onReceiveListTrackedDevicesResult(DeviceIndexPayload deviceIndexPayload) {
		if (deviceIndexPayload == null || isPendingCancel()) {
			isPendingCancel = false;
			isActive = false;
			return;
		}

		store.clear();
		for (Device device : deviceStore.getDevices()) {
			DeviceTrackingState state = new DeviceTrackingState(device.getId(), TrackingState.INACTIVE);
			if (deviceIndexPayload.getIndex().contains(device.getHashedHardwareIdentifier().toString())) {
				state.setTrackingState(TrackingState.TRACKED);
			}
			store.add(state);
		}

		isActive = false;
	}
}
