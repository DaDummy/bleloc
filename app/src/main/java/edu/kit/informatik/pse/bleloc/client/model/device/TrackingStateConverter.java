package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.TypeConverter;

class TrackingStateConverter {
	@TypeConverter
	public static Integer fromTrackingState(TrackingState trackingState) {
		return trackingState.getCode();
	}

	@TypeConverter
	public static TrackingState integerToTrackingState(Integer code) {
		return TrackingState.fromInteger(code);
	}
}
