package edu.kit.informatik.pse.bleloc.model;

public interface DeviceStoreListener {
	/**
	 * New device entry added.
	 *
	 * @param device
	 * 		the new device entry
	 */
	public void onAddDevice(Device device);

	/**
	 * Device entry removed.
	 *
	 * @param device
	 * 		the removed device entry no longer in the store
	 */
	public void onRemoveDevice(Device device);
}
