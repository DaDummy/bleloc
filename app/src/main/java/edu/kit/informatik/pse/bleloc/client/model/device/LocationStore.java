package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.*;

import java.util.List;

/**
 * Store class for associatiion of devices and location data.
 */
@Dao
public abstract class LocationStore extends SynchronizableStore<Location> {
	@Query("SELECT * FROM location")
	public abstract List<Location> getSyncIndex();

	@Query("SELECT * FROM location WHERE deviceId = :deviceId AND isDeleted = 0 ORDER BY date DESC")
	public abstract List<Location> getByDevice(long deviceId);

	@Query("DELETE FROM location")
	public abstract  void clear();
}
