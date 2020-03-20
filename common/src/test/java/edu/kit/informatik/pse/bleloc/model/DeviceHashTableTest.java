package edu.kit.informatik.pse.bleloc.model;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class DeviceHashTableTest {
	private final HashedMacAddress testAddress1 = HashedMacAddress.fromByteArray(new byte[]{1});
	private final HashedMacAddress testAddress2 = HashedMacAddress.fromByteArray(new byte[]{2});

	@Test(expected = IllegalArgumentException.class)
	public void createInvalid() {
		DeviceHashTable invalidTable = new DeviceHashTable(Long.MAX_VALUE);
	}

	@Test
	public void empty() {
		DeviceHashTable dht = new DeviceHashTable(1);
		Assert.assertFalse(dht.contains(testAddress1));
	}

	@Test
	public void addAndContains() {
		DeviceHashTable dht = new DeviceHashTable(1);
		Assert.assertTrue(dht.add(testAddress1));
		Assert.assertTrue(dht.contains(testAddress1));
	}

	@Test
	public void timestamping() throws InterruptedException {
		DeviceHashTable dht = new DeviceHashTable(1);
		Date date1 = dht.getLastUpdateTime();
		Thread.sleep(1000);
		Assert.assertTrue(dht.add(testAddress1));
		Date date2 = dht.getLastUpdateTime();
		Assert.assertTrue(date2.after(date1));
	}

	@Test
	public void serializeEmpty() throws IOException {
		DeviceHashTable src = new DeviceHashTable(1);
		DeviceHashTable dht = new DeviceHashTable(src.getLastUpdateTime(), src.getSerialized());
		Assert.assertFalse(dht.contains(testAddress1));
	}

	@Test
	public void serializeContains() throws IOException {
		DeviceHashTable src = new DeviceHashTable(1);
		Assert.assertTrue(src.add(testAddress1));
		DeviceHashTable dht = new DeviceHashTable(src.getLastUpdateTime(), src.getSerialized());
		Assert.assertTrue(dht.contains(testAddress1));
	}

	@Test
	public void addToFullTable(){
		DeviceHashTable dht = new DeviceHashTable(1);
		Assert.assertTrue(dht.add(testAddress1));
		Assert.assertFalse(dht.add(testAddress2));
	}
}