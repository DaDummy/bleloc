package edu.kit.informatik.pse.bleloc.client.controller;

import androidx.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ContextTest {
	private Context context;

	@Before
	public void setUp() {
		this.context = new Context();
		this.context.onCreate();
	}

	@After
	public void tearDown() {
		this.context = null;
	}

	@Test
	public void getRequestManager() {
		Assert.assertNotNull(this.context.getRequestManager());
	}

	@Test
	public void getAuthenticationManager() {
		Assert.assertNotNull(this.context.getAuthenticationManager());
	}

	@Test
	public void getUserDataSyncManager() {
		Assert.assertNotNull(this.context.getUserDataSyncManager());
	}

	@Test
	public void getTrackingStateManager() {
		Assert.assertNotNull(this.context.getTrackingStateManager());
	}

	@Test
	public void getDatabase() {
		Assert.assertNotNull(this.context.getDatabase());
	}

	@Test
	public void getDeviceHashTableStore() {
		Assert.assertNotNull(this.context.getDeviceHashTableStore());
	}

	@Test
	public void getScanner() {
		Assert.assertNotNull(this.context.getScanner());
	}

	@Test
	public void getBackgroundScanManager() {
		Assert.assertNotNull(this.context.getBackgroundScanManager());
	}

	@Test
	public void getScanResultUploadManager() {
		Assert.assertNotNull(this.context.getScanResultUploadManager());
	}

	@Test
	public void getSettings() {
		Assert.assertNotNull(this.context.getSettings());
	}
}
