package edu.kit.informatik.pse.bleloc.cdi;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedUserAccount;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

@RequestScoped
public class AuthenticatedUserAccountProducer {

	@Produces
	@RequestScoped
	@AuthenticatedUserAccount
	UserAccountProxy authenticatedUserAccount = new UserAccountProxy();

	public void handleEvent(@Observes @AuthenticatedUserAccount UserAccountProxy accountProxy) {
		authenticatedUserAccount.setAccount(accountProxy.getAccount());
	}
}
