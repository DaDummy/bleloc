package edu.kit.informatik.pse.bleloc.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class GeneralStoreTest {
	private static GeneralStore store;
	private static UserDataStore userDataStore;
	private static EntityManager em;

	private static final UserAccount illegalEntity = new UserAccount("1", "1");

	private static class Store extends GeneralStore {
		protected Store(EntityManager em) {
			super(em);
		}
	};

	@Entity
	private class Entry extends AbstractEntry {
		@GeneratedValue
		@Id
		private Long id;

		private void testPrivate() {}
		public void testPublic() {
			throw new RuntimeException();
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
		store = new Store(em);
		userDataStore = new UserDataStore(em);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setEntityType_IllegalEntity() {
		store.setEntityType(String.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addEntry_IllegalEntity() throws NoSuchMethodException {
		Method m = String.class.getDeclaredMethod("length");
		store.addEntry(illegalEntity, new Method[]{m});
	}

	@Test(expected = IllegalArgumentException.class)
	public void addEntry_IllegalAccess() throws NoSuchMethodException {
		store.setEntityType(Entry.class);
		Method m = Entry.class.getDeclaredMethod("testPrivate");
		store.addEntry(new Entry(), new Method[]{m});
	}

	@Test(expected = IllegalArgumentException.class)
	public void addEntry_IllegalMethod() throws NoSuchMethodException {
		store.setEntityType(Entry.class);
		Method m = Entry.class.getDeclaredMethod("testPublic");
		store.addEntry(new Entry(), new Method[]{m});
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateEntity_IllegalEntity() throws NoSuchMethodException {
		Method m = String.class.getDeclaredMethod("length");
		store.updateEntity(illegalEntity, new Method[]{m});
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateEntity_IllegalAccess() throws NoSuchMethodException {
		store.setEntityType(Entry.class);
		Method m = Entry.class.getDeclaredMethod("testPrivate");
		store.updateEntity(new Entry(), new Method[]{m});
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateEntity_IllegalMethod() throws NoSuchMethodException {
		store.setEntityType(Entry.class);
		Method m = Entry.class.getDeclaredMethod("testPublic");
		store.updateEntity(new Entry(), new Method[]{m});
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeEntity_IllegalEntity() throws NoSuchMethodException {
		Method m = String.class.getDeclaredMethod("length");
		store.removeEntity(illegalEntity, new Method[]{m});
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeEntity_IllegalAccess() throws NoSuchMethodException {
		store.setEntityType(Entry.class);
		Method m = Entry.class.getDeclaredMethod("testPrivate");
		store.removeEntity(new Entry(), new Method[]{m});
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeEntity_IllegalMethod() throws NoSuchMethodException {
		store.setEntityType(Entry.class);
		Method m = Entry.class.getDeclaredMethod("testPublic");
		store.removeEntity(new Entry(), new Method[]{m});
	}

	@Test
	public void getEntry_TransactionIntegrity() {
		em.getTransaction().begin();
		try {
			store.getEntry(1L);
		}
		catch (Exception e) {}
		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		em.getTransaction().rollback();
	}

	@Test
	public void getListPagination_TransactionIntegrity() {
		em.getTransaction().begin();
		try {
			store.getListPagination(1, 1);
		}
		catch (Exception e) {}
		// Since transactions are container managed the transaction should still be active
		Assert.assertTrue(em.getTransaction().isActive());

		em.getTransaction().rollback();
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyEntity_IllegalEntity() {
		store.verifyEntity(String.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getMethod_NoSuchMethod() {
		store.getMethod(String.class, "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyEntity_NoSuchMethod() {
		store.verifyEntity(new Entry());
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyEntity_IllegalAccess() {

		@Entity
		class Entry extends AbstractEntry {
			@GeneratedValue
			@Id
			private Long id;

			private Long getId() {return id;}
			private void testPrivate() {}
			public void testPublic() {
				throw new RuntimeException();
			}
		};

		store.verifyEntity(new Entry());
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyEntity_InvocationTargetException() {

		@Entity
		class Entry extends AbstractEntry {
			@GeneratedValue
			@Id
			private Long id;

			public Long getId() {throw new RuntimeException();}
			private void testPrivate() {}
			public void testPublic() {
				throw new RuntimeException();
			}
		};

		store.verifyEntity(new Entry());
	}
}