package edu.kit.informatik.pse.bleloc.cdi;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedBackofficeAccount;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

@RequestScoped
@SuppressWarnings("unused")
public class AuthenticatedBackofficeAccountProducer {

	@Produces
	@RequestScoped
	@AuthenticatedBackofficeAccount
	BackofficeAccountProxy authenticatedBackofficeAccount = new BackofficeAccountProxy();

	public void handleEvent(@Observes @AuthenticatedBackofficeAccount BackofficeAccountProxy accountProxy) {
		authenticatedBackofficeAccount.setAccount(accountProxy.getAccount());
	}
}
