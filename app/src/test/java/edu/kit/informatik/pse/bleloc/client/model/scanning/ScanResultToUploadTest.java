package edu.kit.informatik.pse.bleloc.client.model.scanning;

import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ScanResultToUploadTest {

	private static final long ExpectedId = 15;
	private static final Date ExpectedDate = new Date();
	private static final HashedMacAddress ExpectedHashedHardwareIdentifier =
		HashedMacAddress.fromString("0123456789ABCDEF0123456789ABCDEF");
	private static final HashedMacAddress SomeHashedHardwareIdentifier =
		HashedMacAddress.fromString("00000000000000000000000000000000");
	private static final byte[] ExpectedEncryptedLocationData =
		new byte[]{(byte) 0xfe, (byte) 0xdc, (byte) 0xba, (byte) 0x98, 0x76, 0x54, 0x32, 0x10};
	private static final byte[] SomeEncryptedLocationData = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

	@Test
	public void setId() {
		ScanResultToUpload scanResultToUpload = new ScanResultToUpload(SomeHashedHardwareIdentifier, SomeEncryptedLocationData);
		scanResultToUpload.setId(ExpectedId);
		Assert.assertEquals(ExpectedId, scanResultToUpload.getId());
	}

	@Test
	public void setDate() {
		ScanResultToUpload scanResultToUpload = new ScanResultToUpload(SomeHashedHardwareIdentifier, SomeEncryptedLocationData);
		scanResultToUpload.setDate(ExpectedDate);
		Assert.assertEquals(ExpectedDate, scanResultToUpload.getDate());
	}

	@Test
	public void setHashedHardwareIdentifier() {
		ScanResultToUpload scanResultToUpload = new ScanResultToUpload(SomeHashedHardwareIdentifier, SomeEncryptedLocationData);
		scanResultToUpload.setHashedHardwareIdentifier(ExpectedHashedHardwareIdentifier);
		Assert.assertEquals(ExpectedHashedHardwareIdentifier, scanResultToUpload.getHashedHardwareIdentifier());

		scanResultToUpload = new ScanResultToUpload(ExpectedHashedHardwareIdentifier, ExpectedEncryptedLocationData);
		Assert.assertEquals(ExpectedHashedHardwareIdentifier, scanResultToUpload.getHashedHardwareIdentifier());
	}

	@Test
	public void setEncryptedLocationData() {
		ScanResultToUpload scanResultToUpload = new ScanResultToUpload(SomeHashedHardwareIdentifier, SomeEncryptedLocationData);
		scanResultToUpload.setEncryptedLocationData(ExpectedEncryptedLocationData);
		Assert.assertEquals(ExpectedEncryptedLocationData, scanResultToUpload.getEncryptedLocationData());

		scanResultToUpload = new ScanResultToUpload(ExpectedHashedHardwareIdentifier, ExpectedEncryptedLocationData);
		Assert.assertEquals(ExpectedEncryptedLocationData, scanResultToUpload.getEncryptedLocationData());
	}
}