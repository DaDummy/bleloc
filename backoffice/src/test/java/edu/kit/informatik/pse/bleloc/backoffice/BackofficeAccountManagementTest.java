package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.model.BackofficeAccount;
import edu.kit.informatik.pse.bleloc.model.BackofficeAccountStore;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BackofficeAccountManagementTest extends BackofficeTest {
	private BackofficeAccountManagement backofficeAccountManagement;

	public BackofficeAccountManagementTest() {
		backofficeAccountManagement = new BackofficeAccountManagement();
		injectObjectsInto(backofficeAccountManagement);

		em.getTransaction().begin();
		BackofficeAccountStore userStore = new BackofficeAccountStore(em);
		userStore.add(new BackofficeAccount("correct", "correct"));
		userStore.add(new BackofficeAccount("correct2", "correct2"));
		em.getTransaction().commit();
	}

	@Before
	public void setUp() {
		em.getTransaction().begin();
	}

	@After
	public void tearDown() {
		em.getTransaction().rollback();
	}

	@Test
	public void testShowList() {
		Assert.assertNotNull(backofficeAccountManagement.showList(10,1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShowListInvalid() {
		backofficeAccountManagement.showList(10,0);
	}

	@Test
	public void testShowDetails() {
		Assert.assertNotNull(backofficeAccountManagement.showDetails("correct"));
	}

	@Test
	public void testShowDetailsInvalid() {
		backofficeAccountManagement.showDetails("wrong");
	}

	@Test
	public void testDoDelete() {
		backofficeAccountManagement.doDelete("correct2");
		backofficeAccountManagement.doDelete("correct");
	}

	@Test
	public void testShowCreateForm() {
		Assert.assertNotNull(backofficeAccountManagement.showCreateForm());
	}

	@Test
	public void testDoCreate() {
		Assert.assertNotNull(backofficeAccountManagement.doCreate("new", "new", "new"));
	}
	@Test
	public void testDoCreateNoRepeat() {
		Assert.assertNotNull(backofficeAccountManagement.doCreate("new", "new", ""));
	}

	@Test
	public void testShowChangePwForm() {
		Assert.assertNotNull(backofficeAccountManagement.showChangePwForm("correct"));
	}

	@Test
	public void testDoChangePw() {
		Assert.assertNotNull(backofficeAccountManagement.doChangePw("correct", "new", "new"));
	}

	@Test
	public void testDoChangePwNoRepeat() {
		Assert.assertNotNull(backofficeAccountManagement.doChangePw("correct", "new", ""));
	}
}
