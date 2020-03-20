package edu.kit.informatik.pse.bleloc.client.model.typeconverters;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

	@TypeConverter
	public static Date convertTimestampToDate(Long timestamp) {
		return timestamp == null ? null : new Date(timestamp);
	}

	@TypeConverter
	public static Long convertDateToTimestamp(Date date) {
		return date == null ? null : date.getTime();
	}
}
