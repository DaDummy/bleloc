package edu.kit.informatik.pse.bleloc.model;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Date;

import java.util.GregorianCalendar;

public class DeviceTest {
	private static EntityManager em = null;
	private static final HashedMacAddress testHashId = HashedMacAddress.fromString("000000000000");
	private UserAccount testUser = new UserAccount("Alice", "12345678");
	private Device testDevice = new Device(testUser, testHashId);;

	@BeforeClass
	public static void setUp() {
		// set up a persistence context with test db
		if (em == null) {
			em = Persistence.
				                createEntityManagerFactory("testDB").
				                createEntityManager();
		}
		//close any open transactions
		if(em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
	}

	@Before
	public void createTestEntry() {
		em.getTransaction().begin();
		em.persist(testUser);
		em.persist(testDevice);
		em.getTransaction().commit();

	}

	@After
	public void cleanDatabase() {
		// truncate test tables
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Device").executeUpdate();
		em.createQuery("DELETE FROM UserAccount").executeUpdate();
		em.getTransaction().commit();
	}
	@AfterClass
	public static void tearDown() {
		em.close();
	}

	@Test
	public void getId() {
		Assert.assertNotNull(this.testDevice.getId());
	}

	@Test
	public void getTrackUntil() {

		em.getTransaction().begin();
		Date setDate = (Date) em.createQuery("SELECT date FROM Device WHERE user = :user", Date.class).
			setParameter("user", this.testUser).
			  getSingleResult();
		em.getTransaction().commit();
		long dateDiff = 30 * 86400000L;
		Assert.assertEquals(dateDiff, testDevice.getTrackUntil().getTime() - setDate.getTime());
	}

	@Test
	public void setTrackUntil() {
		em.getTransaction().begin();
		Date newDate = new GregorianCalendar(2019, 0, 5).getTime();
		this.testDevice.setTrackUntil(newDate);
		em.persist(testDevice);
		em.flush();
		Date trackUntil = (Date) em.createQuery("SELECT trackUntil FROM Device WHERE user = :user", Date.class).
			setParameter("user", this.testUser).
			                           getSingleResult();
		em.getTransaction().commit();
		Assert.assertEquals(trackUntil.getTime(), newDate.getTime());
	}

	@Test
	public void getUserAccount() {
		Assert.assertEquals(this.testUser, this.testDevice.getUserAccount());
	}

	@Test
	public void getHardwareIdentifier() {
		Assert.assertEquals(this.testHashId, this.testDevice.getHardwareIdentifier());
	}

	@Test
	public void testEquals() {
		Device device2;
		Device device3 = new Device(testUser, HashedMacAddress.fromString("000000000001"));

		em.detach(testDevice);

		em.getTransaction().begin();
		device2 = em.find(Device.class, testDevice.getId());
		em.getTransaction().commit();

		Assert.assertFalse(testDevice == device2);
		Assert.assertEquals(testDevice, device2);
		Assert.assertNotEquals(testDevice, device3);
		Assert.assertNotEquals(testDevice, null);
		Assert.assertEquals(testDevice, testDevice);

		//check hashes
		Assert.assertEquals(testDevice.hashCode(), device2.hashCode());
		Assert.assertNotEquals(testDevice.hashCode(), null);
		Assert.assertNotEquals(testDevice.hashCode(), device3);
	}
}