package edu.kit.informatik.pse.bleloc.client.model.typeconverters;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

public class DateConverterTest {
	private static final Date date = new GregorianCalendar(2019, 1, 1,12,0,0).getTime();
	private static final long timestamp = 1549018800000L;

	@Test
	public void convertTimestampToDateTest(){
		Assert.assertEquals(date, DateConverter.convertTimestampToDate(timestamp));
	}

	@Test
	public void convertTimestampToDateTestNull(){
		Assert.assertEquals(null,
		                    DateConverter.convertTimestampToDate(null));
	}

	@Test
	public void convertDateToTimestampTest(){
		long timestampTest = DateConverter.convertDateToTimestamp(date);
		Assert.assertEquals(timestamp, timestampTest);
	}

	@Test
	public void convertDateToTimestampTestNull(){
		Assert.assertEquals(null,
		                    DateConverter.convertDateToTimestamp(null));
	}

}
