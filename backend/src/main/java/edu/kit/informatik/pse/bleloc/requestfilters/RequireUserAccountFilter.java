package edu.kit.informatik.pse.bleloc.requestfilters;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedUserAccount;
import edu.kit.informatik.pse.bleloc.annotations.RequireUserAccount;
import edu.kit.informatik.pse.bleloc.cdi.UserAccountProxy;
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
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@RequestScoped
@RequireUserAccount
@Provider
@Priority(Priorities.AUTHENTICATION)
public class RequireUserAccountFilter implements ContainerRequestFilter {
	public static final String CookieName = "userAuth";
	public static final int CookieMaxAge = Integer.MAX_VALUE;

	// TODO(ca) We might want to move this someplace else...
	public static final Authenticator ActiveAuthenticator =
		new WebTokenAuthenticatorFactory().createAuthenticator(AuthenticatorPurpose.UserAccountAuthentication, UserAccount.InvalidId);

	@Inject
	@AuthenticatedUserAccount
	Event<UserAccountProxy> userAccountAuthenticatedEvent;

	@PersistenceContext
	EntityManager em;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		UserAccount account = null;

		Cookie cookie = requestContext.getCookies().get(CookieName);
		if (cookie != null) {
			// Verify cookie and retrieve the stored account id
			Long accountId = ActiveAuthenticator.verifyCookie(cookie.getValue());

			UserAccountStore accountStore = new UserAccountStore(em);

			// Now look up the associated account
			account = accountStore.get(accountId);
		}

		// If we managed to look up the account successfully, the user is authenticated
		if (account != null) {
			// Make sure our annotation is fed the right object in that case
			userAccountAuthenticatedEvent.fire(new UserAccountProxy(account));
		} else {
			// Abort processing and return a response with HTTP status unauthorized (401)
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
	}
}
