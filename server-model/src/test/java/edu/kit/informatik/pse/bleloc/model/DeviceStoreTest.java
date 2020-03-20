package edu.kit.informatik.pse.bleloc.model;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.fail;

public class DeviceStoreTest {
	private static EntityManager em = null;
	private static DeviceStore entryStore;

	private UserAccount testUser1;
	private HashedMacAddress hashId;
	private Device device1;

	protected int onAddDeviceCallCount = 0;
	protected int onRemoveDeviceCallCount = 0;

	protected DeviceStoreListener dummyListener = new DeviceStoreListener() {
		@Override
		public void onAddDevice(Device device) {
			++onAddDeviceCallCount;
		}

		@Override
		public void onRemoveDevice(Device device) {
			++onRemoveDeviceCallCount;
		}
	};

	@BeforeClass
	public static void setUp() {
		// set up a persistence context with test db
		if (em == null) {
			em = Persistence.
				                createEntityManagerFactory("testDB").
				                createEntityManager();
		}
		// create an entity store to be used by the tests
		entryStore = new DeviceStore(em);
	}

	@Before
	public void createInitialEntities() {
		em.getTransaction().begin();
		//check if database empty
		if((em.createQuery("SELECT COUNT(*) FROM UserAccount", Long.class).getSingleResult() != 0) ||
		   (em.createQuery("SELECT COUNT(*) FROM Device", Long.class).getSingleResult() != 0)) {
			em.createQuery("DELETE FROM UserAccount").executeUpdate();
			em.createQuery("DELETE FROM Device").executeUpdate();
		}
		// create initial  database with a user and two associated data entries
		this.testUser1 = new UserAccount("Alice", "12345678");
		em.persist(this.testUser1);
		this.hashId = HashedMacAddress.fromString("000000000000");
		this.device1 = new Device(testUser1, hashId);
		em.getTransaction().commit();
	}

	@After
	public void tearDown() {
		// truncate test tables
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Device").executeUpdate();
		em.createQuery("DELETE FROM UserAccount").executeUpdate();
		em.getTransaction().commit();
	}

	@AfterClass
	public static void close() {
		em.close();
	}

	@Test
	public void add_Normal() {
		entryStore.registerDeviceStoreListener(dummyListener);

		entryStore.add(device1);
		Assert.assertNotNull(device1.getId());

		//verify listener
		Assert.assertEquals(1, onAddDeviceCallCount);
		Assert.assertEquals(0, onRemoveDeviceCallCount);
	}

	@Test
	public void remove_Normal() {
		em.getTransaction().begin();
		em.persist(device1);
		em.getTransaction().commit();

		entryStore.registerDeviceStoreListener(dummyListener);

		entryStore.remove(device1);

		//verify listener
		Assert.assertEquals(0, onAddDeviceCallCount);
		Assert.assertEquals(1, onRemoveDeviceCallCount);

		em.getTransaction().begin();
		Query q = em.createQuery("FROM Device d where d.id = :id", Device.class);
		q.setParameter("id", device1.getId());
		List<Device> result = (List<Device>) q.getResultList();
		em.getTransaction().commit();
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void removeByUserAccount() {
		em.getTransaction().begin();
		em.persist(device1);
		em.getTransaction().commit();

		entryStore.registerDeviceStoreListener(dummyListener);

		entryStore.removeByUserAccount(testUser1);

		//verify listener
		Assert.assertEquals(0, onAddDeviceCallCount);
		Assert.assertEquals(1, onRemoveDeviceCallCount);

		em.getTransaction().begin();
		Query q = em.createQuery("FROM Device d where user = :user", Device.class);
		q.setParameter("user", testUser1);
		List<Device> result = (List<Device>) q.getResultList();
		em.getTransaction().commit();
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void removeByUserAccount_BatchDelete() {
		em.getTransaction().begin();
		//create a whole bunch of devices
		ArrayList<Device> deviceList = new ArrayList<>();
		byte[] hashAddr = new byte[6];
		for (int i = 0; i < 110; i++) {
			new Random().nextBytes(hashAddr);
			Device d = new Device(testUser1, HashedMacAddress.fromByteArray(hashAddr));
			deviceList.add(d);
			entryStore.add(d);
		}

		// Register and deregister listener
		entryStore.registerDeviceStoreListener(dummyListener);
		entryStore.deregisterDeviceStoreListener(dummyListener);

		//delete all
		entryStore.removeByUserAccount(testUser1);
		em.getTransaction().commit();

		//verify listener
		Assert.assertEquals(0, onAddDeviceCallCount);
		Assert.assertEquals(0, onRemoveDeviceCallCount);

		//verify deleted
		em.getTransaction().begin();
		Query q = em.createQuery("FROM Device d where user = :user", Device.class);
		q.setParameter("user", testUser1);
		List<Device> result = (List<Device>) q.getResultList();
		em.getTransaction().commit();
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void get() {
		em.getTransaction().begin();
		em.persist(device1);
		em.getTransaction().commit();
		Device result = entryStore.get(testUser1, hashId);
		Assert.assertEquals(device1, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIllegalEntity() {
		UserAccount user2 = new UserAccount("Alex", "123456");
		entryStore.get(user2, hashId);
	}

	@Test
	public void getByHardwareIdentifer() {
		em.getTransaction().begin();
		em.persist(device1);
		em.getTransaction().commit();
		Iterable<Device> result = entryStore.getByHardwareIdentifer(hashId);
		Assert.assertEquals(device1, result.iterator().next());
	}

	@Test
	public void getAllByUserAccount() {
		em.getTransaction().begin();
		em.persist(device1);
		em.getTransaction().commit();
		ArrayList<Device> result = (ArrayList<Device>) entryStore.getAllByUserAccount(testUser1);
		Assert.assertEquals(device1, result.get(0));
	}

	@Test
	public void getAllHardwareIdentifiers() {
		Device device2 = new Device(testUser1, HashedMacAddress.fromString("000000000001"));
		ArrayList<HashedMacAddress> entryList = new ArrayList<>();
		entryList.add(device1.getHardwareIdentifier());
		entryList.add(device2.getHardwareIdentifier());
		em.getTransaction().begin();
		em.persist(device1);
		em.persist(device2);
		em.getTransaction().commit();
		Iterable<HashedMacAddress> resultList = entryStore.getAllHardwareIdentifiers();
		// for every entry in resultList remove appropriate Entry in local userList
		for (HashedMacAddress hashedMacAddress : resultList) {
			if(!entryList.remove(hashedMacAddress)) {
				fail();
			}
		}
		// local userList should be empty now
		Assert.assertEquals(entryList.size(), 0);
	}

	@Test
	public void getCount() {
		em.getTransaction().begin();
		em.persist(device1);
		em.getTransaction().commit();
		long result = entryStore.getCount();
		Assert.assertEquals(result, 1);
	}

	@Test
	public void getCountByUserAccount() {
		em.getTransaction().begin();
		em.persist(device1);
		em.getTransaction().commit();
		long result = entryStore.getCountByUserAccount(testUser1);
		Assert.assertEquals(result, 1);
	}



	@Test
	public void testTransactionIntegrity() {
		try {
			// start transaction
			em.getTransaction().begin();
			entryStore.removeByUserAccount(testUser1);
		}
		catch (Exception e) {}

		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		try {
			entryStore.get(testUser1, hashId);
		}
		catch (Exception e) {}

		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		try {
			entryStore.getAllHardwareIdentifiers();
		}
		catch (Exception e) {}

		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		em.getTransaction().rollback();
	}
}