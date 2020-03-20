package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.model.DeviceHashTableStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeviceHashTableInitializerTest extends ControllerTest {
	DeviceHashTableInitializer initializer;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// There is no active transaction in WebListeners
		em.getTransaction().rollback();

		// Create instance and emulate dependency injection
		initializer = new DeviceHashTableInitializer();
		initializer.em = em;
		initializer.tx = generateUserTransaction();
		initializer.deviceHashTableStore = new DeviceHashTableStore();
	}

	@Test
	public void contextInitialized() {
		Assert.assertNull(initializer.deviceHashTableStore.get());

		initializer.contextInitialized(null);

		Assert.assertNotNull(initializer.deviceHashTableStore.get());
	}

	@Test
	public void contextDestroyed() {
		// Currently this method does nothing, so there is nothing to check
		initializer.contextDestroyed(null);
	}
}