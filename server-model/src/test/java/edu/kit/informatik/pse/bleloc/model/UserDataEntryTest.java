package edu.kit.informatik.pse.bleloc.model;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class UserDataEntryTest {
	private static EntityManager em = null;
	UserDataEntry entry;
	Date date1;
	byte[] data1;
	UserAccount user1;

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
		this.user1 = new UserAccount("Alice", "12345678");
		em.getTransaction().begin();
		em.persist(user1);
		this.date1 = new GregorianCalendar(2019, 0, 5).getTime();
		this.data1 = "Test".getBytes();
		this.entry = new UserDataEntry(this.date1, this.data1, this.user1);
		em.persist(this.entry);
		em.getTransaction().commit();
	}

	@After
	public void cleanDatabase() {
		// truncate test tables
		em.getTransaction().begin();
		em.createQuery("DELETE FROM UserDataEntry").executeUpdate();
		em.createQuery("DELETE FROM UserAccount").executeUpdate();
		em.getTransaction().commit();
	}
	@AfterClass
	public static void tearDown() {
		em.close();
	}

	/**
	 * Helper function persists an entity
	 * @param entry a new user account
	 * @return a managed user account
	 */
	private UserDataEntry persistEntity(UserDataEntry entry) {
		//start transaction
		em.getTransaction().begin();
		//persist entity
		em.persist(entry);
		//end transaction
		em.getTransaction().commit();
		//return persistent entity
		return entry;
	}

	private boolean sameDay(Date date1, Date date2) {
		System.out.println("Same Day: " + date1+ "||" + date2);
		return date1.getTime() == date2.getTime();
	}

	@Test
	public void getId() {
		//check id
		Assert.assertNotNull(entry.getId());
	}

	@Test
	public void getModifiedAt() {
		Assert.assertTrue(sameDay(entry.getModifiedAt(), this.date1));
	}

	@Test
	public void setModifiedAt() {
		Date now = new Date();
		this.entry.setModifiedAt(now);
		this.entry = persistEntity(this.entry);
		Assert.assertTrue(sameDay(entry.getModifiedAt(), now));
	}

	@Test
	public void getEncryptedData() {
		Assert.assertArrayEquals(this.entry.getEncryptedData(), this.data1);
	}

	@Test
	public void setEncryptedData() {
		byte[] someData = "FooBar".getBytes();
		this.entry.setEncryptedData(someData);
		this.entry = persistEntity(this.entry);
		Assert.assertArrayEquals(this.entry.getEncryptedData(), someData);
	}

	@Test
	public void getUser() {
		Assert.assertEquals(this.entry.getUser(), this.user1);
	}

	@Test
	public void testEquals() {
		em.detach(entry);

		UserDataEntry entry2;
		em.getTransaction().begin();
		Query q = em.createQuery(
			"FROM UserDataEntry WHERE user = :userParam and id = :idParam",
			UserDataEntry.class);
		q.setParameter("userParam", user1);
		q.setParameter("idParam", entry.getId());
		entry2 = (UserDataEntry) q.getSingleResult();
		em.getTransaction().commit();

		UserDataEntry entry3 = new UserDataEntry(new GregorianCalendar(2011, 1, 1).getTime(), data1, user1);

		Assert.assertFalse(entry == entry2);
		Assert.assertEquals(entry, entry2);
		Assert.assertNotEquals(entry, entry3);
		Assert.assertNotEquals(entry, null);
		Assert.assertEquals(entry, entry);

		//check hashes
		Assert.assertEquals(entry.hashCode(), entry2.hashCode());
		Assert.assertNotEquals(entry.hashCode(), entry3.hashCode());
		Assert.assertNotEquals(entry.hashCode(), null);
	}

}