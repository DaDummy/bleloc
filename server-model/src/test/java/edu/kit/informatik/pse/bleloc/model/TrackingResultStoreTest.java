package edu.kit.informatik.pse.bleloc.model;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

import java.util.*;

import static org.junit.Assert.*;

public class TrackingResultStoreTest {
	private static EntityManager em = null;
	private static TrackingResultStore entryStore;

	private UserAccount testUser1;
	private Device device1;
	private TrackingResult trackingResult1;

	@BeforeClass
	public static void setUp() {
		// set up a persistence context with test db
		if (em == null) {
			em = Persistence.
				                createEntityManagerFactory("testDB").
				                createEntityManager();
		}
		// create an entity store to be used by the tests
		entryStore = new TrackingResultStore(em);
	}

	@Before
	public void createInitialEntities() {
		em.getTransaction().begin();
		//check if database empty
		if((em.createQuery("SELECT COUNT(*) FROM UserAccount", Long.class).getSingleResult() != 0) ||
		   (em.createQuery("SELECT COUNT(*) FROM Device", Long.class).getSingleResult() != 0)) {
			em.createQuery("DELETE FROM TrackingResult").executeUpdate();
			em.createQuery("DELETE FROM Device").executeUpdate();
			em.createQuery("DELETE FROM UserAccount").executeUpdate();
		}
		// create initial  database with a user and two associated data entries
		this.testUser1 = new UserAccount("Alice", "12345678");
		em.persist(this.testUser1);
		this.device1 = new Device(testUser1, HashedMacAddress.fromString("000000000000"));
		em.persist(device1);
		this.trackingResult1 = new TrackingResult(
			device1,
			new GregorianCalendar(2019, 1, 1).getTime(),
			"test1".getBytes());
		em.getTransaction().commit();
	}

	@After
	public void tearDown() {
		// truncate test tables
		em.getTransaction().begin();
		em.createQuery("DELETE FROM TrackingResult").executeUpdate();
		em.createQuery("DELETE FROM Device").executeUpdate();
		em.createQuery("DELETE FROM UserAccount").executeUpdate();
		em.getTransaction().commit();
	}

	@AfterClass
	public static void close() {
		em.close();
	}

	@Test
	public void add() {
		entryStore.add(trackingResult1);
		Assert.assertNotNull(trackingResult1.getId());
	}

	@Test
	public void remove() {
		em.getTransaction().begin();
		em.persist(trackingResult1);
		em.getTransaction().commit();
		entryStore.remove(trackingResult1);
		em.getTransaction().begin();
		Query q = em.createQuery("FROM TrackingResult d where d.id = :id", TrackingResult.class);
		q.setParameter("id", trackingResult1.getId());
		List<TrackingResult> result = (List<TrackingResult>) q.getResultList();
		em.getTransaction().commit();
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void removeByDevice() {
		em.getTransaction().begin();
		em.persist(trackingResult1);
		em.getTransaction().commit();
		//test delete
		entryStore.removeByDevice(device1);
		//verify
		em.getTransaction().begin();
		Query q = em.createQuery("FROM TrackingResult d where device = :deviceParam", TrackingResult.class);
		q.setParameter("deviceParam", device1);
		List<TrackingResult> result = (List<TrackingResult>) q.getResultList();
		em.getTransaction().commit();
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void removeByDevice_BatchDelete() {
		TrackingResult entry;
		Date testDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(testDate);

		//create a ton of entries
		em.getTransaction().begin();
		for(int i = 0; i <= 110; i++) {
			calendar.add(Calendar.HOUR, 1);
			entry = new TrackingResult(
				device1,
				calendar.getTime(),
				"test".getBytes()
			);
			em.persist(entry);
		}
		em.getTransaction().commit();
		//test delete
		em.getTransaction().begin();
		entryStore.removeByDevice(device1);
		em.getTransaction().commit();
		//verify
		em.getTransaction().begin();
		Query q = em.createQuery("FROM TrackingResult d where device = :deviceParam", TrackingResult.class);
		q.setParameter("deviceParam", device1);
		List<TrackingResult> result = (List<TrackingResult>) q.getResultList();
		em.getTransaction().commit();
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void get() {
		em.getTransaction().begin();
		em.persist(trackingResult1);
		em.getTransaction().commit();
		TrackingResult result = entryStore.get(trackingResult1.getId());
		Assert.assertEquals(trackingResult1, result);
	}

	@Test
	public void getNthOldestByUserAccount() {
		Date date = new GregorianCalendar(2019, 1, 2).getTime();
		TrackingResult trackingResult2 = new TrackingResult(
			device1,
			date,
			"test1".getBytes());
		TrackingResult trackingResult3 = new TrackingResult(
			device1,
			new GregorianCalendar(2019, 1, 3).getTime(),
			"test1".getBytes());
		em.getTransaction().begin();
		em.persist(trackingResult2);
		em.persist(trackingResult1);
		em.persist(trackingResult3);
		em.getTransaction().commit();
		TrackingResult result = entryStore.getNthOldestByUserAccount(testUser1, 1);
		if (result == null) {
			fail("Result was null");
		}
		Assert.assertEquals(date.getTime(), result.getEncounteredAt().getTime());
	}

	@Test
	public void getNthOldestByUserAccount_emptyResult() {
		TrackingResult result = entryStore.getNthOldestByUserAccount(testUser1, 1);
		Assert.assertEquals(null, result);
	}

	@Test
	public void testTransactionIntegrity() {
		try {
			// start transaction
			em.getTransaction().begin();
			entryStore.removeByDevice(device1);
		}
		catch (Exception e) {}
		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		try {
			entryStore.getNthOldestByUserAccount(testUser1, 1);
		}
		catch (Exception e) {}
		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		em.getTransaction().rollback();
	}
}