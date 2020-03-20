package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.*;
import androidx.annotation.NonNull;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.DateConverter;

import java.util.Date;

/**
 * This class represents a geolocation entry and all information that was collected there.
 */
@Entity(tableName = "location", foreignKeys = @ForeignKey(entity=Device.class, parentColumns="id", childColumns="deviceId", onDelete=ForeignKey.CASCADE))
public class Location extends SynchronizableObject {

	@ColumnInfo(index = true)
	private long deviceId;

	@NonNull
	@TypeConverters(DateConverter.class)
	private Date date;

	private int signalStrength;

	private int intLongitude;

	private int intLatitude;

	private boolean seen;

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * Gets the date and time at which the location data was collected.
	 *
	 * @return Date of collection
	 */
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the Location coordinate
	 */
	public int getIntLongitude() {
		return intLongitude;
	}

	/**
	 * Gets the Location coordinate
	 */
	public int getIntLatitude() {
		return intLatitude;
	}

	public void setIntLongitude(int lon) {
		this.intLongitude = lon;
	}

	public void setIntLatitude(int lat) {
		this.intLatitude = lat;
	}

	public double getLongitude() {
		return intLongitude * (180. / 0x7fffffff);
	}

	public double getLatitude() {
		return intLatitude * (90. / 0x7fffffff);
	}

	public void setLongitude(double lon) {
		this.intLongitude = (int)(lon * (0x7fffffff / 180.));
	}

	public void setLatitude(double lat) {
		this.intLatitude = (int)(lat * (0x7fffffff / 90.));
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	/**
	 * Gets the signal strength measured at the location.
	 */
	public int getSignalStrength() {
		return signalStrength;
	}

	/**
	 * Checks if the location is new, or has already been viewed.<br> A location's initial status is <code>seen() ==
	 * false</code>. As soon as the user checks out the new locations, their status is set to <code>seen() ==
	 * true</code> via markSeen().
	 *
	 * @return <code>true</code> for a location that has been viewed, <code>false</code> for a new location
	 */
	public boolean isSeen() {
		return seen;
	}

	/**
	 * Sets the status from "new location" to "checked".
	 *
	 * @see #isSeen()
	 */
	public void setSeen(boolean state) {
		this.seen = state;
	}
}
