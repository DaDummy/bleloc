package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.*;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Association between a Device and a TrackingState.
 */
@Entity(
	tableName = "trackingState",
	foreignKeys = @ForeignKey(entity=Device.class, parentColumns="id", childColumns="deviceId", onDelete = CASCADE),
	indices = {@Index(value = "deviceId", unique = true)})
public class DeviceTrackingState {
	@PrimaryKey
	private long deviceId;

	@TypeConverters(TrackingStateConverter.class)
	private TrackingState trackingState;

	//private Date trackUntil;


	/**
	 * Creates a new association between a Device and a TrackingState.
	 *  @param deviceId
	 * 		The device to assign the state to
	 * @param trackingState
	 */
	public DeviceTrackingState(long deviceId, TrackingState trackingState) {
		this.deviceId = deviceId;
		this.trackingState = trackingState;
	}

	/**
	 * Returns the device in this Device-TrackingState-association.
	 *
	 * @return The device in this association
	 */
	public long getDeviceId() {
		return deviceId;
	}

	/**
	 * Sets the tracking state.
	 *
	 * @param state
	 * 		the new tracking state
	 */
	public void setTrackingState(TrackingState state) {
		this.trackingState = state;
	}

	/**
	 * Gets the tracking state.
	 *
	 * @return the current TrackingState.
	 */
	public TrackingState getTrackingState() {
		return trackingState;
	}
}
