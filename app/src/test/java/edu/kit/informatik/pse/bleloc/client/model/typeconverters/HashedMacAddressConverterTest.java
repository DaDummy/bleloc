package edu.kit.informatik.pse.bleloc.client.model.typeconverters;

import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.junit.Assert;
import org.junit.Test;

public class HashedMacAddressConverterTest {

	private static final byte[] array = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	private static final HashedMacAddress hashedMacAddress = HashedMacAddress.fromByteArray(array);

	@Test
	public void convertByteArrayToHashedMacAddressTest(){
		Assert.assertEquals(hashedMacAddress, HashedMacAddressConverter.convertByteArrayToHashedMacAddress(array));
	}

	@Test
	public void convertHashedMacAddressToByteArrayTestNull(){
		Assert.assertEquals(null, HashedMacAddressConverter.convertHashedMacAddressToByteArray(null));
	}

	@Test
	public void convertHashedMacAddressToByteArrayTest(){
		Assert.assertEquals(array, HashedMacAddressConverter.convertHashedMacAddressToByteArray(hashedMacAddress));
	}

}
