package edu.kit.informatik.pse.bleloc.model.settings;

import org.junit.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import java.util.List;

public class SettingStoreTest {
	private static EntityManager em = null;
	private static SettingStore store;

	private static final String testTextId = "testId";
	private static final String testGroupTextId = "testGroupId";
	private static final String testTitle = "title";

	private Setting testSetting = Setting.createSetting(
		testTextId,
		testGroupTextId,
		testTitle,
		"foobar",
		Setting.Type.TEXT,
		"foo"
	);

	private Setting identicalTestSetting = Setting.createSetting(
		testTextId,
		testGroupTextId,
		testTitle,
		"foobar",
		Setting.Type.TEXT,
		"foo"
	);

	@BeforeClass
	public static void setUpClass() {
		// set up a persistence context with test db
		if (em == null) {
			em = Persistence.
				                createEntityManagerFactory("testDB").
				                createEntityManager();
		}
		// create an entity store to be used by the tests
		store = new SettingStore(em);
	}

	@Before
	public void setUp() {
		em.getTransaction().begin();
	}

	@After
	public void tearDown() {
		em.flush();
		// Reset database
		em.getTransaction().rollback();
	}

	@AfterClass
	public static void tearDownClass() {
		em.close();
	}

	@Test
	public void add_Normal() {
		store.add(testSetting);
		Assert.assertNotNull(testSetting.getId());
	}

	@Test (expected = EntityExistsException.class)
	public void add_AlreadyExists() {
		store.add(testSetting);
		store.add(identicalTestSetting);
	}

	@Test
	public void update_Normal() {
		store.add(testSetting);
		em.flush();
		store.update(testSetting.getGroupTextId(), testSetting.getTextId(), "foofoobarbar");
	}

	@Test (expected = EntityNotFoundException.class)
	public void update_NonExistent() {
		store.update(testSetting.getGroupTextId(), testSetting.getTextId(), "foofoobarbar");
	}

	@Test
	public void getGroupByName() {
		em.persist(testSetting);

		Long expectedId = testSetting.getId();
		Long resultId = store.get(testGroupTextId, testTextId).getId();
		Assert.assertEquals(expectedId, resultId);
	}

	@Test
	public void getAllSettings_Empty() {
		List<Setting> list = store.getAllSettings();
		assert  list.size() == 0;
	}
}
