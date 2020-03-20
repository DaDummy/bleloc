package edu.kit.informatik.pse.bleloc.cdi;

import edu.kit.informatik.pse.bleloc.model.BackofficeAccount;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticatedBackofficeAccountProducerTest {

	@Test
	public void handleEvent() {
		AuthenticatedBackofficeAccountProducer producer = new AuthenticatedBackofficeAccountProducer();
		Assert.assertNotNull(producer.authenticatedBackofficeAccount);

		BackofficeAccount backofficeAccount = new BackofficeAccount("test", "123");
		producer.handleEvent(new BackofficeAccountProxy(backofficeAccount));
		Assert.assertEquals(backofficeAccount, producer.authenticatedBackofficeAccount.getAccount());
	}
}