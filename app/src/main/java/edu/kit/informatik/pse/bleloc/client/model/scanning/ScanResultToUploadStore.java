package edu.kit.informatik.pse.bleloc.client.model.scanning;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Collection of location entries.
 */
@Dao
public interface ScanResultToUploadStore {

	/**
	 * Returns the entry that was added before all other entries.
	 *
	 * @return The oldest entry
	 */
	@Query("SELECT * FROM scan_result_to_upload ORDER BY id ASC LIMIT 1")
	public ScanResultToUpload getOldest();

	/**
	 * Adds a new entry to the store.
	 *
	 * @param scanResultToUpload
	 * 		The element to add
	 */
	@Insert
	public void add(ScanResultToUpload scanResultToUpload);

	/**
	 * Removes an entry from the store.<br> The entry must be given by direct reference.
	 *
	 * @param scanResultToUpload
	 * 		The entry to delete
	 */
	@Delete
	public void delete(ScanResultToUpload scanResultToUpload);
}
