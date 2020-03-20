package edu.kit.informatik.pse.bleloc.cdi;

import edu.kit.informatik.pse.bleloc.model.UserAccount;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserAccountProxyTest {

	@Test
	public void setAndGetAccount() {
		UserAccountProxy proxy = new UserAccountProxy();
		proxy.setAccount(null);
		Assert.assertNull(proxy.getAccount());

		UserAccount userAccount = new UserAccount("test", "123");
		proxy.setAccount(userAccount);
		Assert.assertEquals(userAccount, proxy.getAccount());

		proxy = new UserAccountProxy(userAccount);
		Assert.assertEquals(userAccount, proxy.getAccount());
	}

}