package edu.kit.informatik.pse.bleloc.client.model.scanning;

import androidx.room.Room;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.controller.AppDatabase;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScanResultToUploadStoreTest {
	private static final HashedMacAddress SomeHashedHardwareIdentifier =
		HashedMacAddress.fromString("0123456789ABCDEF0123456789ABCDEF");
	private static final byte[] SomeEncryptedLocationData =
		new byte[]{(byte) 0xfe, (byte) 0xdc, (byte) 0xba, (byte) 0x98, 0x76, 0x54, 0x32, 0x10};
	private static final HashedMacAddress OtherHashedHardwareIdentifier =
		HashedMacAddress.fromString("00000000000000000000000000000000");
	private static final byte[] OtherEncryptedLocationData = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

	private ScanResultToUploadStore store;
	private AppDatabase database;

	@Before
	public void setUp() {
		Context context = ApplicationProvider.getApplicationContext();
		database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
		store = database.getScanResultToUploadStore();
	}

	@After
	public void tearDown() {
		database.close();
	}

	@Test
	public void testSingleAddGet() {
		ScanResultToUpload scanResultToUpload =
			new ScanResultToUpload(SomeHashedHardwareIdentifier, SomeEncryptedLocationData);
		store.add(scanResultToUpload);

		ScanResultToUpload oldest = store.getOldest();

		assertEquals(scanResultToUpload, oldest);
	}

	@Test
	public void testMultipleAddDeleteGet() {
		ScanResultToUpload someScanResultToUpload =
			new ScanResultToUpload(SomeHashedHardwareIdentifier, SomeEncryptedLocationData);
		ScanResultToUpload otherScanResultToUpload =
			new ScanResultToUpload(OtherHashedHardwareIdentifier, OtherEncryptedLocationData);
		Assert.assertNotEquals(someScanResultToUpload, otherScanResultToUpload);

		ScanResultToUpload oldest = store.getOldest();
		Assert.assertNull(oldest);

		store.add(someScanResultToUpload);
		oldest = store.getOldest();
		assertEquals(someScanResultToUpload, oldest);

		store.add(otherScanResultToUpload);
		oldest = store.getOldest();
		assertEquals(someScanResultToUpload, oldest);

		store.delete(oldest);
		oldest = store.getOldest();
		assertEquals(otherScanResultToUpload, oldest);

		store.delete(oldest);
		oldest = store.getOldest();
		Assert.assertNull(oldest);
	}

	private static void assertEquals(ScanResultToUpload expected, ScanResultToUpload actual) {
		if (expected == actual || expected == null || actual == null) {
			Assert.assertEquals(expected, actual);
		}
		Assert.assertEquals(expected.getDate(), actual.getDate());
		Assert.assertEquals(expected.getHashedHardwareIdentifier(), actual.getHashedHardwareIdentifier());
		Assert.assertArrayEquals(expected.getEncryptedLocationData(), actual.getEncryptedLocationData());
	}
}