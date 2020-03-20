package edu.kit.informatik.pse.bleloc.client.model.scanning;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.DateConverter;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.HashedMacAddressConverter;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;

import java.util.Date;

/**
 * This class represents a geolocation entry and all information that was collected there.
 */
@Entity(tableName = "scan_result_to_upload")
public class ScanResultToUpload {

	@PrimaryKey(autoGenerate = true)
	private long id;

	@NonNull
	@TypeConverters(DateConverter.class)
	private Date date;

	@NonNull
	@TypeConverters(HashedMacAddressConverter.class)
	private HashedMacAddress hashedHardwareIdentifier;

	@NonNull
	private byte[] encryptedLocationData;

	/**
	 * Initializes a ScanResultToUpload with device identifier and data.
	 *
	 * @param hashedHardwareIdentifier
	 * 		The found devices identifier
	 * @param encryptedLocationData
	 * 		The devices data
	 */
	public ScanResultToUpload(@NonNull HashedMacAddress hashedHardwareIdentifier,
	                          @NonNull byte[] encryptedLocationData) {
		this.hashedHardwareIdentifier = hashedHardwareIdentifier;
		this.date = new Date();
		this.encryptedLocationData = encryptedLocationData;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the date and time at which the location data was collected.
	 *
	 * @return Date of collection
	 */
	public @NonNull
	Date getDate() {
		return date;
	}

	public void setDate(@NonNull Date date) {
		this.date = date;
	}

	/**
	 * Gets the Location coordinate
	 */
	public @NonNull
	HashedMacAddress getHashedHardwareIdentifier() {
		return hashedHardwareIdentifier;
	}

	public void setHashedHardwareIdentifier(@NonNull HashedMacAddress hashedHardwareIdentifier) {
		this.hashedHardwareIdentifier = hashedHardwareIdentifier;
	}

	/**
	 * Gets the signal strength measured at the location.
	 */
	public @NonNull
	byte[] getEncryptedLocationData() {
		return this.encryptedLocationData;
	}

	public void setEncryptedLocationData(@NonNull byte[] encryptedLocationData) {
		this.encryptedLocationData = encryptedLocationData;
	}
}
