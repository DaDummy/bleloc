package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedUserAccount;
import edu.kit.informatik.pse.bleloc.annotations.RequireUserAccount;
import edu.kit.informatik.pse.bleloc.cdi.UserAccountProxy;
import edu.kit.informatik.pse.bleloc.model.UserAccount;
import edu.kit.informatik.pse.bleloc.model.UserAccountStore;
import edu.kit.informatik.pse.bleloc.payload.ChangePasswordRequestPayload;
import edu.kit.informatik.pse.bleloc.payload.LoginCredentialsPayload;
import edu.kit.informatik.pse.bleloc.requestfilters.RequireUserAccountFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * Handles requests regarding user account management and authentication
 */
@Path(value = "user")
@Stateless
public class UserAccountManagement {

	@Inject
	@AuthenticatedUserAccount
	UserAccountProxy accountProxy;

	@Context
	HttpServletRequest request;

	@PersistenceContext
	EntityManager em;

	@POST
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "login")
	public Response postLogin(LoginCredentialsPayload credentials) {
		UserAccountStore accountStore = new UserAccountStore(em);

		// Now get the account the user tries to log in to
		UserAccount account = accountStore.get(credentials.getName());

		// Check if the account exists
		if (account != null) {
			// Verify credentials...
			boolean areCredentialsValid = account.verifyPassword(credentials.getPassword());

			if (areCredentialsValid) {
				// Credentials are valid - perform the login

				// Build the response and report success
				return Response.ok().cookie(generateLoginCookie(account.getId())).build();
			}
		}

		// Login failed - build the appropriate response
		return Response.status(Response.Status.FORBIDDEN).build();
	}

	@POST
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "register")
	public Response postRegister(LoginCredentialsPayload credentials) {
		if (credentials.getName().isEmpty() == false) {
			UserAccountStore accountStore = new UserAccountStore(em);

			if (accountStore.get(credentials.getName()) == null) {
				UserAccount account = new UserAccount(credentials.getName(), credentials.getPassword());

				// Now get the account the user tries to log in to
				accountStore.add(account);

				// Build the response and report success
				return Response.ok().cookie(generateLoginCookie(account.getId())).build();
			} else {
				return Response.status(Response.Status.CONFLICT).build();
			}
		}

		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@RequireUserAccount
	@POST
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "delete")
	public Response postDelete() {
		UserAccountStore accountStore = new UserAccountStore(em);

		// Delete the currently logged in account
		accountStore.remove(accountStore.get(accountProxy.getAccount().getId()));

		// Report success
		return Response.ok().build();
	}

	@RequireUserAccount
	@POST
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "changePassword")
	public Response postChangePassword(ChangePasswordRequestPayload payload) {
		UserAccountStore accountStore = new UserAccountStore(em);

		UserAccount account = accountStore.get(accountProxy.getAccount().getId());

		// Verify if old password was provided correctly
		boolean isOldPasswordCorrect = account.verifyPassword(payload.getOldPassword());

		if (isOldPasswordCorrect) {
			// Update account object
			account.changePassword(payload.getNewPassword());

			// Persist changes
			accountStore.update(account);

			// Report success
			return Response.ok().build();
		}

		// Report failure due to client error
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	private NewCookie generateLoginCookie(Long accountId) {
		// Create the login cookie
		String cookieString = RequireUserAccountFilter.ActiveAuthenticator.createCookie(accountId);

		return new NewCookie(RequireUserAccountFilter.CookieName,
		                     cookieString,
		                     request.getContextPath(),
		                     null,
		                     null,
		                     RequireUserAccountFilter.CookieMaxAge,
		                     false);
	}
}
