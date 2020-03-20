package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.model.BackofficeAccount;
import edu.kit.informatik.pse.bleloc.model.BackofficeAccountStore;
import edu.kit.informatik.pse.bleloc.payload.LoginCredentialsPayload;
import edu.kit.informatik.pse.bleloc.requestfilters.RequireBackofficeAccountFilter;
import org.glassfish.jersey.server.mvc.Template;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("auth")
@Stateless
public class Authentication {

	@PersistenceContext
	@SuppressWarnings("unused")
	private EntityManager em;

	@Context
	@SuppressWarnings("unused")
	private HttpServletRequest request;

	/**
	 * Shows a login form.
	 *
	 * @return the form
	 */
	@GET
	@Produces("text/html")
	@Template(name = "/auth/login.ftl")
	public Map<String, Object> showLoginForm() {
		Map<String, Object> map = new HashMap<>();
		return map;
	}

	/**
	 * Logs the user in. Sends back JWT.
	 *
	 * @return the redirect to /menu
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("login")
	public Response doLogin(@FormParam("login_name") String name, @FormParam("login_password") String password) {
		BackofficeAccountStore accountStore = new BackofficeAccountStore(em);

		// Now get the account the user tries to log in to
		BackofficeAccount account = accountStore.get(name);

		// Check if the account exists
		if (account != null) {
			// Verify credentials...
			boolean areCredentialsValid = account.verifyPassword(password);

			if (areCredentialsValid) {
				// Credentials are valid - perform the login

				// Create the login cookie
				String cookieString = RequireBackofficeAccountFilter.ActiveAuthenticator.createCookie(account.getId());

				// Build the response and report success
				return Response.seeOther(UriBuilder.fromResource(Home.class).build())
				               .cookie(new NewCookie(RequireBackofficeAccountFilter.CookieName, cookieString,
				                                     request.getContextPath(),
				                                     null,
				                                     null,
				                                     NewCookie.DEFAULT_MAX_AGE,
				                                     false)).build();
			}
		}

		// Login failed - build the appropriate response
		return Response.seeOther(UriBuilder.fromResource(Authentication.class).build()).build();
	}

	/**
	 * Clears JWT.
	 *
	 * @return the redirect to /auth
	 */
	@POST
	@Path("logout")
	public Response doLogout(@Context HttpHeaders httpHeaders) {
		Response.ResponseBuilder response = Response.seeOther(UriBuilder.fromResource(Authentication.class).build());
		Cookie cookie = httpHeaders.getCookies().get(RequireBackofficeAccountFilter.CookieName);
		if (cookie != null) {
			cookie = new Cookie(RequireBackofficeAccountFilter.CookieName, null,
			                    request.getContextPath(), null);
			response.cookie(new NewCookie(cookie, null, 0, false));
		}
		return response.build();
	}
}
