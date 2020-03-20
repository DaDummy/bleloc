package edu.kit.informatik.pse.bleloc.model;

import edu.kit.informatik.pse.bleloc.payload.UserDataIndexEntry;
import org.junit.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.fail;

public class UserDataStoreTest {
	private static EntityManager em = null;
	private static UserDataStore entryStore;

	private UserAccount testUser1;
	private UserDataEntry testEntry1;
	private UserDataEntry testEntry2;

	@BeforeClass
	public static void setUp() {
		// set up a persistence context with test db
		if (em == null) {
			em = Persistence.
				createEntityManagerFactory("testDB").
				createEntityManager();
		}
		// create an entity store to be used by the tests
		entryStore = new UserDataStore(em);
	}

	@Before
	public void createInitialEntities() {
		em.getTransaction().begin();
		//check if database empty
		if((em.createQuery("SELECT COUNT(*) FROM UserAccount", Long.class).getSingleResult() != 0) ||
		   (em.createQuery("SELECT COUNT(*) FROM UserDataEntry", Long.class).getSingleResult() != 0)) {
			em.createQuery("DELETE FROM UserAccount").executeUpdate();
		}
		// create initial  database with a user and two associated data entries
		this.testUser1 = new UserAccount("Alice", "12345678");
		em.persist(this.testUser1);
		this.testEntry1 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 1).getTime(),
			"test1".getBytes(),
			this.testUser1);
		this.testEntry2 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 2).getTime(),
			"test2".getBytes(),
			this.testUser1);
		em.persist(this.testEntry1);
		em.persist(this.testEntry2);
		em.getTransaction().commit();
	}

	@After
	public void tearDown() {
		// truncate test tables
		em.getTransaction().begin();
		em.createQuery("DELETE FROM UserDataEntry").executeUpdate();
		em.createQuery("DELETE FROM UserAccount").executeUpdate();
		em.getTransaction().commit();
	}

	@AfterClass
	public static void close() {
		em.close();
	}

	@Test
	public void addNewEntry_Normal() {
		UserDataEntry testEntry3 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 3).getTime(),
			"test3".getBytes(), testUser1);
		entryStore.add(testEntry3);
		Assert.assertNotNull(testEntry3.getId());
	}

	@Test
	public void addNewEntry_UserDetached() {
		em.detach(testUser1);

		UserDataEntry testEntry3 =
			new UserDataEntry(new GregorianCalendar(2019, 1, 3).getTime(), "test3".getBytes(), testUser1);
		entryStore.add(testEntry3);
		Assert.assertNotNull(testEntry3.getId());
	}

	@Test(expected = EntityNotFoundException.class)
	public void addNewEntry_UserTransient() {
		UserAccount testUser2 = new UserAccount("Alex", "12345678");
		UserDataEntry testEntry3 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 3).getTime(),
			"test3".getBytes(),
			testUser2);
		entryStore.add(testEntry3);
	}

	@Test
	public void update_Normal() {
		Date newDate = new GregorianCalendar(2019, 2, 2).getTime();
		this.testEntry2.setModifiedAt(newDate);
		entryStore.update(testEntry2);
		Assert.assertEquals(testEntry2.getModifiedAt(), newDate);
	}

	@Test(expected = EntityNotFoundException.class)
	public void update_Transient() {
		UserDataEntry testEntry3 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 3).getTime(),
			"test3".getBytes(), testUser1);
		entryStore.update(testEntry3);
	}

	@Test(expected = EntityNotFoundException.class)
	public void update_TransientUser() {
		UserDataEntry testEntry3 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 3).getTime(),
			"test3".getBytes(),
			new UserAccount("Bob", "123456789"));
		entryStore.update(testEntry3);
	}

	@Test(expected = EntityNotFoundException.class)
	public void addNewEntry_deletedUser() {
		em.remove(testUser1);
		UserDataEntry testEntry3 =
			new UserDataEntry(new GregorianCalendar(2019, 1, 3).getTime(), "test3".getBytes(), testUser1);
		entryStore.update(testEntry3);
	}

	@Test
	public void remove_Normal() {
		//it is necessary to invoke another operation to test this behavior
		UserDataEntry testEntry3 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 3).getTime(),
			"test3".getBytes(), testUser1);
		entryStore.add(testEntry3);
		entryStore.remove(testEntry3);
		Assert.assertNull(em.find(UserDataEntry.class, testEntry3.getId()));
	}

	@Test
	public void remove_Transient() {
		UserDataEntry testEntry3 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 3).getTime(),
			"test3".getBytes(), testUser1);
		entryStore.remove(testEntry3);
		Assert.assertNull(testEntry3.getId());
	}

	@Test
	public void remove_TransientUser() {
		UserDataEntry testEntry3 = new UserDataEntry(
			new GregorianCalendar(2019, 1, 3).getTime(),
			"test3".getBytes(),
			new UserAccount("Bob", "123456789"));
		entryStore.remove(testEntry3);
		Assert.assertNull(testEntry3.getId());
	}

	@Test
	public void removeByUserAccount_Normal() {
		em.getTransaction().begin();
		entryStore.removeByUserAccount(testUser1);
		ArrayList<UserDataEntry> resultList = (ArrayList<UserDataEntry>) em.
			createQuery("FROM UserDataEntry WHERE userId = :userId", UserDataEntry.class).
			setParameter("userId", testUser1.getId()).
			getResultList();
		Assert.assertTrue(resultList.size() == 0);
		em.getTransaction().rollback();
	}

	@Test
	public void removeByUserAccount_Transient() {
		UserAccount testUser2 = new UserAccount("Alex", "12345678");
		entryStore.removeByUserAccount(testUser2);
		Assert.assertNull(testUser2.getId());
	}

	@Test
	public void removeByUserAccount_EmptySet() {
		//create and persist new user
		UserAccount testUser2 = new UserAccount("Alex", "12345678");
		em.getTransaction().begin();
		em.persist(testUser2);
		em.getTransaction().commit();
		entryStore.removeByUserAccount(testUser2);
		ArrayList<UserDataEntry> resultList = (ArrayList<UserDataEntry>) em.
			createQuery("FROM UserDataEntry WHERE userId = :userId", UserDataEntry.class).
			setParameter("userId", testUser2.getId()).
			getResultList();
		Assert.assertEquals(resultList.size(), 0);
	}

	@Test
	public void removeByUserAccount_BatchDelete() {
		//create a ton of entries
		UserDataEntry entry;
		em.getTransaction().begin();
		for(int i = 0; i <= 110; i++) {
			entry = new UserDataEntry(
				new GregorianCalendar(2019, 1, 3).getTime(),
				"test".getBytes(),
				this.testUser1);
			em.persist(entry);
		}

		//test delete
		entryStore.removeByUserAccount(testUser1);
		ArrayList<UserDataEntry> resultList = (ArrayList<UserDataEntry>) em.
			 createQuery("FROM UserDataEntry WHERE userId = :userId", UserDataEntry.class).
			 setParameter("userId", testUser1.getId()).
			 getResultList();
		Assert.assertTrue(resultList.size() == 0);

		em.getTransaction().rollback();
	}

	@Test
	public void get_Normal() {
		UserDataEntry result = entryStore.get(this.testUser1, this.testEntry1.getId());
		Assert.assertEquals(result, this.testEntry1);
	}

	@Test
	public void get_Empty() {
		UserDataEntry result = entryStore.get(this.testUser1, 2L);
		Assert.assertNull(result);
	}

	@Test(expected = EntityNotFoundException.class)
	public void get_InvalidUser() {
		UserAccount user2 = new UserAccount("Alex", "12345678");
		UserDataEntry result = entryStore.get(user2, this.testEntry1.getId());
	}

	@Test
	public void getIndexByUserAccount_ResultSize() {
		ArrayList<UserDataEntry> resultList = (ArrayList) entryStore.getIndexByUserAccount(this.testUser1);
		Assert.assertEquals(resultList.size(), 2);
	}

	@Test
	public void getIndexByUserAccount_ResultCompleteness() {
		//create a list to compare to
		ArrayList<Long> entryList = new ArrayList<>();
		entryList.add(testEntry1.getId());
		entryList.add(testEntry2.getId());

		ArrayList<UserDataIndexEntry> resultList = (ArrayList) entryStore.getIndexByUserAccount(this.testUser1);
		// for every entry in resultList remove appropriate Entry in local userList
		for (UserDataIndexEntry indexEntry : resultList) {
			if(!entryList.remove(indexEntry.getId())) {
				fail();
			}
		}
		// local userList should be empty now
		Assert.assertEquals(entryList.size(), 0);
	}

	@Test
	public void getIndexByUserAccount_EmptySet() {
		//create and persist new user
		UserAccount user2 = new UserAccount("Alex", "12345678");
		em.getTransaction().begin();
		em.persist(user2);
		em.getTransaction().commit();
		//check list
		ArrayList<UserDataIndexEntry>resultList = (ArrayList) entryStore.getIndexByUserAccount(user2);
		Assert.assertEquals(resultList.size(), 0);
	}

	@Test(expected = EntityNotFoundException.class)
	public void getIndexByUserAccount_invalidUser() {
		UserAccount user2 = new UserAccount("Alex", "12345678");
		ArrayList<UserDataIndexEntry>resultList = (ArrayList) entryStore.getIndexByUserAccount(user2);
	}

	@Test
	public void testTransactionIntegrity() {
		try {
			// start transaction
			em.getTransaction().begin();
			entryStore.get(testUser1,testEntry1.getId());
		}
		catch (Exception e) {}
		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		try {
			entryStore.removeByUserAccount(testUser1);
		}
		catch (Exception e) {}
		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		try {
			entryStore.getIndexByUserAccount(testUser1);
		}
		catch (Exception e) {}
		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		em.getTransaction().rollback();
	}
}