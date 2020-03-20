package edu.kit.informatik.pse.bleloc.requestfilters;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedBackofficeAccount;
import edu.kit.informatik.pse.bleloc.annotations.RequireBackofficeAccount;
import edu.kit.informatik.pse.bleloc.backoffice.Authentication;
import edu.kit.informatik.pse.bleloc.cdi.BackofficeAccountProxy;
import edu.kit.informatik.pse.bleloc.model.*;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@RequestScoped
@RequireBackofficeAccount
@Provider
@Priority(Priorities.AUTHENTICATION)
public class RequireBackofficeAccountFilter implements ContainerRequestFilter {

	@PersistenceContext
	private EntityManager em;

	public static final String CookieName = "adminAuth";

	// TODO(ca) We might want to move this someplace else...
	public static final Authenticator ActiveAuthenticator = new WebTokenAuthenticatorFactory()
		.createAuthenticator(AuthenticatorPurpose.BackofficeAccountAuthentication, BackofficeAccount.InvalidId);

	@Inject
	@AuthenticatedBackofficeAccount
	Event<BackofficeAccountProxy> backofficeAccountAuthenticatedEvent;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		BackofficeAccount account = null;

		Cookie cookie = requestContext.getCookies().get(CookieName);
		if (cookie != null) {
			// Verify cookie and retrieve the stored account id
			long accountId = ActiveAuthenticator.verifyCookie(cookie.getValue());

			BackofficeAccountStore accountStore = new BackofficeAccountStore(em);

			// Now look up the associated account
			account = accountStore.get(accountId);
		}

		// If we managed to look up the account successfully, the user is authenticated
		if (account != null) {
			// Make sure our annotation is fed the right object in that case
			backofficeAccountAuthenticatedEvent.fire(new BackofficeAccountProxy(account));
		} else {
			// Abort processing and redirect to the login page
			requestContext.abortWith(Response.seeOther(UriBuilder.fromResource(Authentication.class).build()).build());
		}
	}
}
