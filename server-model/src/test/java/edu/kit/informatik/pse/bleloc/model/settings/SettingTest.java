package edu.kit.informatik.pse.bleloc.model.settings;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class SettingTest {
	private static EntityManager em = null;
	private String testGroup = "testGroupTextId";

	private final static String testTextId = "test";
	private final static String testLabel = "label";
	private final static String testDescription = "description";
	private final static String testValue = "value";

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

	@After
	public void tearDown() {
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Setting").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	public void createSetting() {
		Setting setting = Setting.createSetting(
			testTextId, testGroup, testLabel, testDescription, Setting.Type.TEXT, testValue
		);
		Assert.assertNotNull(setting);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSetting_nullValue() {
		Setting setting = Setting.createSetting(
			null, testGroup, testLabel, testDescription, Setting.Type.TEXT, testValue
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSetting_wrongFormat00() {
		Setting setting = Setting.createSetting(
			testTextId, testGroup, testLabel, testDescription, Setting.Type.BOOLEAN, testValue
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSetting_wrongFormat01() {
		Setting setting = Setting.createSetting(
			"", testGroup, testLabel, testDescription, Setting.Type.TEXT, testValue
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSetting_wrongFormat02() {
		Setting setting = Setting.createSetting(
			testTextId, testGroup, "", testDescription, Setting.Type.TEXT, testValue
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSetting_wrongFormat03() {
		Setting setting = Setting.createSetting(
			testTextId, "FOO", testLabel, testDescription, Setting.Type.TEXT, testValue
		);
	}

	@Test
	public void testGetter() {
		Setting setting = Setting.createSetting(
			testTextId, testGroup, testLabel, testDescription, Setting.Type.TEXT, testValue
		);
		Assert.assertEquals(testTextId, setting.getTextId());
		Assert.assertEquals(testGroup, setting.getGroupTextId());
		Assert.assertEquals(testLabel, setting.getLabel());
		Assert.assertEquals(testDescription, setting.getDescription());
		Assert.assertEquals(Setting.Type.TEXT, setting.getType());
		Assert.assertEquals(testValue, setting.getValue());
	}

	@Test
	public void setValue() {
		Setting setting = Setting.createSetting(
			testTextId, testGroup, testLabel, testDescription, Setting.Type.BOOLEAN, "true"
		);
		Assert.assertEquals("true", setting.getValue());
		setting.setValue("false");
		Assert.assertEquals("false", setting.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void setValue_wrongFormat() {
		Setting setting = Setting.createSetting(
			testTextId, testGroup, testLabel, testDescription, Setting.Type.BOOLEAN, "true"
		);
		setting.setValue("42");
	}

	@Test
	public void getId_null() {
		Setting setting = Setting.createSetting(
			testTextId, testGroup, testLabel, testDescription, Setting.Type.TEXT, testValue
		);
		Assert.assertNull(setting.getId());
	}

	@Test
	public void getId_persisted() {
		Setting setting = Setting.createSetting(
			testTextId, testGroup, testLabel, testDescription, Setting.Type.TEXT, testValue
		);
		em.persist(setting);
		Assert.assertNotNull(setting.getId());
	}
}