package edu.kit.informatik.pse.bleloc.client.model.typeconverters;

import org.junit.Assert;
import org.junit.Test;

import java.util.Base64;

public class MacAdressConverterTest {

	private static final String macAddress = "11:11:11:11:11:11";
	private static final byte[] array = {0x11,0x11, 0x11, 0x11, 0x11, 0x11};

	@Test
	public void stringToByteArrayTest() {

		Assert.assertEquals(Base64.getEncoder().encodeToString(array),
		                    Base64.getEncoder().encodeToString(MacAdressConverter.stringToByteArray(macAddress)));
	}

	@Test
	public void byteArrayToStringTest(){
		Assert.assertEquals(macAddress, MacAdressConverter.byteArrayToString(array));
	}

}
