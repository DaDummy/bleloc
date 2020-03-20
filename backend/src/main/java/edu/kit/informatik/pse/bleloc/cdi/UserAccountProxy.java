package edu.kit.informatik.pse.bleloc.cdi;

import edu.kit.informatik.pse.bleloc.model.UserAccount;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class UserAccountProxy {
	private UserAccount account;

	public UserAccountProxy() {

	}

	public UserAccountProxy(UserAccount account) {
		this.account = account;
	}

	public UserAccount getAccount() {
		return account;
	}

	public void setAccount(UserAccount account) {
		this.account = account;
	}
}
