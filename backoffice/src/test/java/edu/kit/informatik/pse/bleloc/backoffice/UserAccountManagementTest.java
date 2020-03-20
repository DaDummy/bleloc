package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.model.UserAccount;
import edu.kit.informatik.pse.bleloc.model.UserAccountStore;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class UserAccountManagementTest extends BackofficeTest {
	private UserAccountManagement userAccountManagement;

	public UserAccountManagementTest() {
		userAccountManagement = new UserAccountManagement();
		injectObjectsInto(userAccountManagement);

		em.getTransaction().begin();
		UserAccountStore userAccountStore = new UserAccountStore(em);
		userAccountStore.add(new UserAccount("correct", "correct"));
		em.getTransaction().commit();
	}

	private UserAccount getFirstUser() {
		return ((List<UserAccount>)userAccountManagement.showList(10, 1).get("accounts")).get(0);
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
		Assert.assertEquals("correct", getFirstUser().getName());
	}

	@Test
	public void testShowListMultiplePages() {
		for (int i = 0; i < 20; i++) {
			UserAccountStore userAccountStore = new UserAccountStore(em);
			userAccountStore.add(new UserAccount("user" + i, "password"));
		}

		Assert.assertNotNull(userAccountManagement.showList(10, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShowListInvalid() {
		userAccountManagement.showList(10,0);
	}

	@Test
	public void testShowDetails() {
		Assert.assertEquals("correct", userAccountManagement.showDetails(getFirstUser().getId()).get("name"));
	}

	@Test
	public void testDoDelete() {
		userAccountManagement.doDelete(getFirstUser().getId());
	}
}
