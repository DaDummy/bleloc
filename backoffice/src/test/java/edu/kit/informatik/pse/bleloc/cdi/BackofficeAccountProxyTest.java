package edu.kit.informatik.pse.bleloc.cdi;

import edu.kit.informatik.pse.bleloc.model.BackofficeAccount;
import org.junit.Assert;
import org.junit.Test;

public class BackofficeAccountProxyTest {

	@Test
	public void setAndGetAccount() {
		BackofficeAccountProxy proxy = new BackofficeAccountProxy();
		proxy.setAccount(null);
		Assert.assertNull(proxy.getAccount());

		BackofficeAccount userAccount = new BackofficeAccount("test", "123");
		proxy.setAccount(userAccount);
		Assert.assertEquals(userAccount, proxy.getAccount());

		proxy = new BackofficeAccountProxy(userAccount);
		Assert.assertEquals(userAccount, proxy.getAccount());
	}
}