package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.*;

/**
 * Container Class for user bluetooth devices.
 */
@Dao
public abstract class DeviceTrackingStateStore {

	/**
	 * Gets the number of registered devices
	 *
	 * @return integer with the number of  devices
	 */
	@Query("SELECT COUNT(*) FROM trackingState")
	public abstract int getSize();

	/**
	 * Adds a new device tracking state.<br> An existing entry with the same device as the input will fail the
	 * operation.
	 *
	 * @param deviceTrackingState
	 * 		The tracking state to add
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	public abstract void add(DeviceTrackingState deviceTrackingState);

	/**
	 * Updates a device's tracking state.<br> An existing entry with the same device as the input will be overwritten.
	 *
	 * @param deviceTrackingState
	 * 		The tracking state to update to
	 */
	@Update
	public abstract void update(DeviceTrackingState deviceTrackingState);

	/**
	 * Removes a DeviceTrackingState from the store.
	 *
	 * @param deviceTrackingState
	 * 		The state to remove
	 */
	@Delete
	public abstract void delete(DeviceTrackingState deviceTrackingState);
	/**
	 * Retrieves the DeviceTrackingState for a given Device.
	 *
	 * @param deviceId
	 * 		The device to get the state for
	 * @return The device's state
	 */
	@Query("SELECT * FROM trackingState WHERE deviceId = :deviceId")
	public abstract DeviceTrackingState getByDevice(long deviceId);

	/**
	 * Clears the store. This deletes all entries.
	 */
	@Query("DELETE FROM trackingState")
	public abstract void clear();
}
