package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.*;

import java.util.List;

/**
 * Container Class for user bluetooth devices.
 */
@Dao
public abstract class DeviceStore extends SynchronizableStore<Device> {
	@Query("SELECT * FROM device")
	public abstract List<Device> getSyncIndex();

	@Query("SELECT * FROM device WHERE id = :id AND isDeleted = 0")
	public abstract Device getDevice(long id);

	@Query("SELECT * FROM device WHERE hardwareIdentifier = :hardwareIdentifier AND isDeleted = 0")
	public abstract Device getDevice(byte[] hardwareIdentifier);

	@Query("SELECT * FROM device WHERE syncId = :syncId AND isDeleted = 0")
	public abstract Device getDeviceBySyncId(long syncId);

	@Query("SELECT * FROM device WHERE isDeleted = 0")
	public abstract List<Device> getDevices();

	@Query("SELECT hardwareIdentifier FROM device WHERE isDeleted = 0 ORDER BY hardwareIdentifier")
	public abstract List<byte[]> getHardwareIdentifiers();

	@Query("DELETE FROM device")
	public abstract  void clear();
}
