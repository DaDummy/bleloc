package edu.kit.informatik.pse.bleloc.client.model.device;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.controller.AppDatabase;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.HashedMacAddressConverter;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.MacAdressConverter;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DeviceStoreTest {
	private AppDatabase database;
	private DeviceStore store;
	private static final byte[]	macAddress1 = new byte[]{(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF};
	private static final byte[] macAddress2 = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};


	@Before
	public void setUp() {
		Context context = ApplicationProvider.getApplicationContext();
		database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
		store = database.getDeviceStore();
	}

	@After
	public void tearDown() {
		database.close();
	}



	@Test
	public void testAdd() {
		Device device = new Device(macAddress1);
		store.add(device);

		Device retrievedDevice = store.getDevice(macAddress1);

		assertArrayEquals(device.getHardwareIdentifier(), retrievedDevice.getHardwareIdentifier());
	}

	@Test
	public void update() {
		Device device = new Device(macAddress1);
		long newId = store.add(device);
		//this is weird: add() does NOT set the generated field parameter id
		device.setId(newId);
		device.setHardwareIdentifier(macAddress2);

		store.update(device);

		Device retrievedDevice = store.getDevice(macAddress2);
		assertArrayEquals(macAddress2, retrievedDevice.getHardwareIdentifier());

		Device retrievedDeviceNull = store.getDevice(macAddress1);
		assertNull(retrievedDeviceNull);
	}

	@Test
	public void delete() {
		Device device = new Device(macAddress1);
		long newId = store.add(device);
		//this is weird: add() does NOT set the generated field parameter id
		device.setId(newId);
		store.delete(device);
		Device retrievedDevice = store.getDevice(macAddress1);
		assertNull(retrievedDevice);
	}

	@Test
	public void getDevices() {
		Device device1 = new Device(macAddress1);
		Device device2 = new Device(macAddress2);

		ArrayList<byte[]> addressList = new ArrayList<> ();
		addressList.add(macAddress1);
		addressList.add(macAddress2);

		store.add(device1);
		store.add(device2);

		ArrayList<Device> resultList = (ArrayList<Device>) store.getDevices();
		assertEquals(2, resultList.size());

		// check list completeness
		ArrayList<byte[]> removeList = new ArrayList<>();
		for (Device device : resultList){
			for (byte[] address : addressList) {
				if(Arrays.equals(address, device.getHardwareIdentifier())) {
					removeList.add(address);
				}
			}
		}
		addressList.removeAll(removeList);
		assertEquals(0, addressList.size());
	}

	@Test
	public void getHardwareIdentifiers() {
		Device device1 = new Device(macAddress1);
		Device device2 = new Device(macAddress2);

		ArrayList<byte[]> addressList = new ArrayList<> ();
		addressList.add(macAddress1);
		addressList.add(macAddress2);

		store.add(device1);
		store.add(device2);

		ArrayList<byte[]> resultList = (ArrayList<byte[]>) store.getHardwareIdentifiers();
		assertEquals(2, resultList.size());

		// check list completeness
		ArrayList<byte[]> removeList = new ArrayList<>();
		for (byte[] result : resultList){
			for (byte[] address : addressList) {
				if(Arrays.equals(address, result)) {
					removeList.add(address);
				}
			}
		}
		addressList.removeAll(removeList);
		assertEquals(0, addressList.size());
	}

	@Test
	public void getSyncIndex() {
		Device device1 = new Device(macAddress1);
		Device device2 = new Device(macAddress2);

		device1.setSyncId(1);
		device2.setSyncId(2);

		store.add(device1);
		store.add(device2);

		ArrayList idList = new ArrayList<>(Arrays.asList(1L, 2L));

		List resultList = store.getSyncIndex();

		// check list completeness
		for (Object o : resultList){
			SynchronizableObject so = (SynchronizableObject)o;
			if(idList.contains(so.getSyncId())) {
				idList.remove(so.getSyncId());
			}
			else{
				fail();
			}
		}
		assertEquals(0, idList.size());
	}
}