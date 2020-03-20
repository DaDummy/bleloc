package edu.kit.informatik.pse.bleloc.model;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class BackofficeAccountTest {
	private static EntityManager em = null;
	String name = "Alice";
	String password = "123456";

	@BeforeClass
	public static void setUp() {
		// set up a persistence context with test db
		if (em == null) {
			em = Persistence.
				                createEntityManagerFactory("testDB").
				                createEntityManager();
		}
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
	public void getId() {
		//create entity
		BackofficeAccount user1 = new BackofficeAccount(this.name, this.password);
		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();
		//check id
		Assert.assertNotNull(user1.getId());
	}

	@Test
	public void getName() {
		//create entity
		BackofficeAccount user1 = new BackofficeAccount(this.name, this.password);
		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();
		//check name
		Assert.assertEquals(user1.getName(), this.name);
	}

	@Test
	public void changePassword() {
		//create entity
		BackofficeAccount user1 = new BackofficeAccount(this.name, this.password);
		em.getTransaction().begin();
		em.persist(user1);

		//change password
		String pw = "qwertz";
		user1.changePassword(pw);
		em.persist(user1);
		em.getTransaction().commit();
		//check password
		Assert.assertTrue(user1.verifyPassword(pw));
	}

	@Test
	public void testEquals() {
		BackofficeAccount user1 = new BackofficeAccount(this.name, this.password);
		BackofficeAccount user2;
		BackofficeAccount user3 = new BackofficeAccount("Alex", "123456");

		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();

		em.detach(user1);

		em.getTransaction().begin();
		user2 = em.find(BackofficeAccount.class, user1.getId());
		em.getTransaction().commit();

		Assert.assertFalse(user1 == user2);
		Assert.assertEquals(user1, user2);
		Assert.assertNotEquals(user1, user3);
		Assert.assertNotEquals(user1, null);
		Assert.assertEquals(user1, user1);
	}
}