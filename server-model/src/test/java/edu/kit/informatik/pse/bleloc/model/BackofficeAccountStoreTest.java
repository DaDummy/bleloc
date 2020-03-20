package edu.kit.informatik.pse.bleloc.model;

import org.junit.*;

import static org.junit.Assert.*;

import javax.persistence.*;
import java.util.ArrayList;

public class BackofficeAccountStoreTest {
	private static BackofficeAccountStore store;
	private static EntityManager em;

	@BeforeClass
	public static void setUp() {
		// set up a persistence context with test db
		if (em == null) {
			em = Persistence.
				                createEntityManagerFactory("testDB").
				                createEntityManager();
		}
		// create an entity store to be used by the tests
		store = new BackofficeAccountStore(em);
	}

	@Before
	public void createInitialEntities() {
		em.getTransaction().begin();
		//check if database empty
		if((em.createQuery("SELECT COUNT(*) FROM BackofficeAccount", Long.class).getSingleResult() != 0)) {
			em.createQuery("DELETE FROM BackofficeAccount").executeUpdate();
		}
		em.getTransaction().commit();
	}

	@After
	public void tearDown() {
		//truncate test table
		em.getTransaction().begin();
		em.createQuery("DELETE FROM BackofficeAccount").executeUpdate();
		em.getTransaction().commit();
	}

	@AfterClass
	public static void close() {
		em.close();
	}

	@Test
	public void add_Normal() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		store.add(user1);
		Assert.assertNotNull(user1.getId());
	}

	@Test
	public void add_Existing() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		BackofficeAccount user2 = new BackofficeAccount("Alice", "12345678");
		Throwable exception = new PersistenceException();
		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();
		try {
			store.add(user2);
		} catch (Exception e) {
			exception = e;
		}
		// test
		Assert.assertEquals(EntityExistsException.class, exception.getClass());
	}

	@Test
	public void update() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "pw1");
		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();
		user1.changePassword("pw2");
		// operation
		store.update(user1);
		// test
		Assert.assertTrue(user1.verifyPassword("pw2"));
	}

	@Test
	public void update_Transient() {
		//create two entries
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		Throwable exception = new PersistenceException();
		try {
			// operation
			store.update(user1);
		} catch (Throwable e) {
			exception = e;
		}
		//test
		Assert.assertEquals(EntityNotFoundException.class, exception.getClass());
	}

	@Test
	public void remove() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();
		store.remove(user1);
		em.getTransaction().begin();
		Query q = em.createQuery("FROM BackofficeAccount WHERE name = :name");
		q.setParameter("name", user1.getName());
		Throwable exception = new PersistenceException();
		try{
			BackofficeAccount result = (BackofficeAccount) q.getSingleResult();
		} catch (NoResultException e) {
			exception = e;
		}
		em.getTransaction().commit();
		Assert.assertEquals(NoResultException.class, exception.getClass());
	}

	@Test
	public void remove_Deleted() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		store.remove(user1);
		em.getTransaction().begin();
		Query q = em.createQuery("FROM BackofficeAccount WHERE name = :name");
		q.setParameter("name", user1.getName());
		Throwable exception = new PersistenceException();
		try{
			BackofficeAccount result = (BackofficeAccount) q.getSingleResult();
		} catch (NoResultException e) {
			exception = e;
		}
		em.getTransaction().commit();
		Assert.assertEquals(NoResultException.class, exception.getClass());
	}

	@Test
	public void getById_Normal() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();
		long id = user1.getId();
		BackofficeAccount user2 = store.get(id);
		Assert.assertEquals(user1, user2);
	}

	@Test
	public void getById_NonExisting() {
		BackofficeAccount user1 = store.get(Long.MAX_VALUE);
		Assert.assertNull(user1);
	}

	@Test
	public void getByName() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();
		BackofficeAccount user2 = store.get("Alice");
		Assert.assertEquals(user1, user2);
	}

	@Test
	public void getByNameNonExisting() {
		BackofficeAccount user1 = store.get("Xerxes");
		Assert.assertNull(user1);
	}

	@Test
	public void getCount() {
		em.getTransaction().begin();
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		BackofficeAccount user2 = new BackofficeAccount("Alex", "12345678");
		store.add(user1);
		store.add(user2);
		Assert.assertEquals(store.getCount(), 2);
		em.getTransaction().rollback();
	}

	@Test
	public void getCountZero() {
		Assert.assertEquals(store.getCount(), 0);
	}

	@Test
	public void list_Size() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		BackofficeAccount user2 = new BackofficeAccount("Alex", "12345678");
		em.getTransaction().begin();
		em.persist(user1);
		em.persist(user2);
		em.getTransaction().commit();
		ArrayList<BackofficeAccount> resultList = (ArrayList<BackofficeAccount>) store.list(10, 1);
		Assert.assertEquals(resultList.size(), 2);
	}

	@Test
	public void list_ZeroSize() {
		ArrayList<BackofficeAccount> resultList = (ArrayList<BackofficeAccount>) store.list(10, 1);
		Assert.assertEquals(resultList.size(), 0);
	}

	@Test
	public void list_invalidBoundaries() {
		UserAccount user1 = new UserAccount("Alice", "12345678");
		UserAccount user2 = new UserAccount("Alex", "12345678");
		em.getTransaction().begin();
		em.persist(user1);
		em.persist(user2);
		em.getTransaction().commit();

		Exception exception = new PersistenceException();
		try {
			ArrayList<BackofficeAccount> resultList = (ArrayList<BackofficeAccount>) store.list(10, -1);
		}
		catch (Exception e) {
			exception = e;
		}
		Assert.assertEquals(IllegalArgumentException.class, exception.getClass());
	}

	@Test
	public void list_Completeness() {
		BackofficeAccount user1 = new BackofficeAccount("Alice", "12345678");
		BackofficeAccount user2 = new BackofficeAccount("Alex", "12345678");
		em.getTransaction().begin();
		em.persist(user1);
		em.persist(user2);
		em.getTransaction().commit();

		ArrayList<String> userList = new ArrayList<>();
		userList.add(user1.getName());
		userList.add(user2.getName());

		ArrayList<BackofficeAccount> resultList = (ArrayList<BackofficeAccount>) store.list(10, 1);
		// for every entry in resultList remove appropriate Entry in local userList
		for (BackofficeAccount user : resultList) {
			if (!userList.remove(user.getName())) {
				fail();
			}
		}
		// local userList should be empty now
		if (userList.size() > 0) {
			fail();
		}
	}

	@Test
	public void testTransactionIntegrity() {
		try {
			// start transaction
			em.getTransaction().begin();
			BackofficeAccount testUser = store.get("Alice");
		}
		catch (Exception e) {}
		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		em.getTransaction().rollback();
	}
}