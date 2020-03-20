package edu.kit.informatik.pse.bleloc.cdi;

import edu.kit.informatik.pse.bleloc.model.BackofficeAccount;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class BackofficeAccountProxy {
	private BackofficeAccount account;

	public BackofficeAccountProxy() {

	}

	public BackofficeAccountProxy(BackofficeAccount account) {
		this.account = account;
	}

	public BackofficeAccount getAccount() {
		return account;
	}

	public void setAccount(BackofficeAccount account) {
		this.account = account;
	}
}
