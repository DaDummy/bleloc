package edu.kit.informatik.pse.bleloc.client.model.device;


import android.bluetooth.BluetoothDevice;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.MacAdressConverter;
import edu.kit.informatik.pse.bleloc.client.model.utils.Encryptor;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeviceTest {
	private Device device;
	private static final String macString = "AA:BB:CC:DD:EE:FF";
	private static final String testName = "testName";
	private static final String testAlias = "testAlias";

	@Before
	public void setupTestObjects() {
		this.device = new Device(MacAdressConverter.stringToByteArray("00:00:00:00:00:00"));
	}

	@Test
	public void testHardwareIdentifier() {
		byte[] newHardwareId = MacAdressConverter.stringToByteArray(macString);
		device.setHardwareIdentifier(newHardwareId);
		Assert.assertArrayEquals(newHardwareId, device.getHardwareIdentifier());
	}

	@Test
	public void getHashedHardwareIdentifier() {
		String hash = "91bf8473c4771f211516bb2f9a81a696"; // hash of "00:00:00:00:00:00"
		Assert.assertEquals(hash, device.getHashedHardwareIdentifier().toString());
	}

	@Test
	public void testAlias() {
		device.setAlias(testAlias);
		Assert.assertEquals(testAlias, device.getAlias());
	}

	@Test
	public void testConstructorFromBTD() {
		//define mock
		BluetoothDevice mockBluetoothDevice = mock(BluetoothDevice.class);
		when(mockBluetoothDevice.getName()).thenReturn(testName);
		when(mockBluetoothDevice.getAddress()).thenReturn(macString);
		//create device
		Device testDevice = new Device(mockBluetoothDevice);
		//test
		Assert.assertEquals(testName, testDevice.getName());
		Assert.assertEquals(testName, testDevice.getAlias());
		Assert.assertArrayEquals(
			MacAdressConverter.stringToByteArray(macString),
			testDevice.getHardwareIdentifier());
	}

	//TODO After implementation
	@Test
	public void getVendor() {
	}
}