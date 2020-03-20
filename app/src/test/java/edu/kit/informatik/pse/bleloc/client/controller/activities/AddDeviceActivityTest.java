package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.os.Bundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AddDeviceActivityTest {

	private AddDeviceActivity addDeviceActivity;

	@Before
	public void setUp() {
		this.addDeviceActivity = new AddDeviceActivity();
	}

	@After
	public void tearDown() {
		this.addDeviceActivity = null;
	}

	@Test
	public void onCreate() {
		Bundle bundle = Mockito.mock(Bundle.class);
		this.addDeviceActivity.onCreate(bundle);
	}

	@Test
	public void onResume() {
		this.addDeviceActivity.onResume();
	}
}
