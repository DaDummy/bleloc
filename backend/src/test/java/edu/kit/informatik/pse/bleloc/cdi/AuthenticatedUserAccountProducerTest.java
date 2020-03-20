package edu.kit.informatik.pse.bleloc.cdi;

import edu.kit.informatik.pse.bleloc.model.UserAccount;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticatedUserAccountProducerTest {

	@Test
	public void handleEvent() {
		AuthenticatedUserAccountProducer producer = new AuthenticatedUserAccountProducer();
		Assert.assertNotNull(producer.authenticatedUserAccount);

		UserAccount userAccount = new UserAccount("test", "123");
		producer.handleEvent(new UserAccountProxy(userAccount));
		Assert.assertEquals(userAccount, producer.authenticatedUserAccount.getAccount());
	}
}