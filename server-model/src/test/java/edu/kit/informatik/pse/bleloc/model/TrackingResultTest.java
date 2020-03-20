package edu.kit.informatik.pse.bleloc.model;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import java.util.Date;
import java.util.GregorianCalendar;

public class TrackingResultTest {
	private static EntityManager em = null;
	private static final Date testDate = new GregorianCalendar(2019, 0, 5).getTime();
	private static final byte[] testData = "Test".getBytes();
	private UserAccount testUser = new UserAccount("Alice", "12345678");
	private Device testDevice = new Device(testUser, HashedMacAddress.fromString("000000000000"));
	private TrackingResult testTrackingResult = new TrackingResult(testDevice, testDate, testData);


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
		em.persist(testTrackingResult);
		em.getTransaction().commit();

	}

	@After
	public void cleanDatabase() {
		// truncate test tables
		em.getTransaction().begin();
		em.createQuery("DELETE FROM TrackingResult").executeUpdate();
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
		Assert.assertNotNull(this.testTrackingResult.getId());
	}

	@Test
	public void getEncounteredAt() {
		Assert.assertEquals(this.testDate, this.testTrackingResult.getEncounteredAt());

	}

	@Test
	public void getDevice() {
		Assert.assertEquals(this.testDevice, testTrackingResult.getDevice());
	}

	@Test
	public void getUserAccount() {
		em.getTransaction().begin();
		UserAccount dbuser = (UserAccount) em.createQuery(
			"SELECT d.user FROM TrackingResult t, Device d WHERE t.id = :id",
			UserAccount.class).
			setParameter("id", testTrackingResult.getId()).
			                             getSingleResult();
		em.getTransaction().commit();
		Assert.assertEquals(testTrackingResult.getUserAccount(), dbuser);
	}

	@Test
	public void getEncryptedData() {
		Assert.assertArrayEquals(this.testData, this.testTrackingResult.getEncryptedData());
	}

	@Test
	public void testEquals() {
		TrackingResult testResult2;
		TrackingResult testResult3 = new TrackingResult(
			testDevice,
			new GregorianCalendar(2010, 1, 1).getTime(),
			"0000".getBytes());

		em.detach(testTrackingResult);

		em.getTransaction().begin();
		testResult2 = em.find(TrackingResult.class, testTrackingResult.getId());
		em.getTransaction().commit();

		Assert.assertFalse(testTrackingResult == testResult2);
		Assert.assertEquals(testTrackingResult, testResult2);
		Assert.assertNotEquals(testTrackingResult, testResult3);
		Assert.assertNotEquals(testTrackingResult, null);
		Assert.assertEquals(testTrackingResult, testTrackingResult);

		//check hashes
		Assert.assertEquals(testTrackingResult.hashCode(), testResult2.hashCode());
		Assert.assertNotEquals(testTrackingResult.hashCode(), null);
		Assert.assertNotEquals(testTrackingResult.hashCode(), testResult3);
	}
}