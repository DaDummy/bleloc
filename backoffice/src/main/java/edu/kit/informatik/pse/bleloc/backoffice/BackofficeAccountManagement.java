package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.annotations.RequireBackofficeAccount;
import edu.kit.informatik.pse.bleloc.model.BackofficeAccount;
import edu.kit.informatik.pse.bleloc.model.BackofficeAccountStore;
import org.glassfish.jersey.server.mvc.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.RollbackException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path(value = "admin")
@Stateless
public class BackofficeAccountManagement {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Shows admin accounts.
	 *
	 * @param page
	 * 		the number of the page shown
	 * @return the list of accounts
	 */
	@GET
	@Path(value = "list/{size}/{page}")
	@Produces("text/html")
	@Template(name = "/admin/list.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showList(@PathParam(value = "size") int size, @PathParam(value = "page") int page) {
		// Variables for FTL
		Map<String, Object> result = new TreeMap<>();

		// Create store entity from persistence context
		BackofficeAccountStore accountStore = new BackofficeAccountStore(em);
		// Get get the accounts to be displayed
		List<BackofficeAccount> accounts = accountStore.list(size, page);
		result.put("accounts", accounts);

		result.put("size", size);
		result.put("page", page);
		result.put("pageCount", (accountStore.getCount() + size - 1) / size);

		return result;
	}

	/**
	 * Shows details abount an admin account.
	 *
	 * @param name
	 * 		the user name of the account
	 * @return account details
	 */
	@GET
	@Path(value = "details/{name}")
	@Produces("text/html")
	@Template(name = "/admin/details.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showDetails(@PathParam(value = "name") java.lang.String name) {
		// Create store entity from persistence context
		BackofficeAccountStore accountStore = new BackofficeAccountStore(em);
		// Now get the account the user tries to get details of
		BackofficeAccount account = accountStore.get(name);

		Map<String, Object> result = new TreeMap<>();
		// insert defaults
		result.put("id", "");
		result.put("name", "");
		// overwrite if account name was valid
		if (account != null) {
			result.put("id", account.getId());
			result.put("name", account.getName());
		}
		return result;
	}

	/**
	 * Deletes an admin account.
	 *
	 * @param name
	 * 		the name of the account
	 * @return redirect to account list
	 */
	@POST
	@Path(value = "delete/{name}")
	@RequireBackofficeAccount
	public Response doDelete(@PathParam(value = "name") java.lang.String name) {
		// Create store entity from persistence context
		BackofficeAccountStore accountStore = new BackofficeAccountStore(em);

		// always keep one account
		if (accountStore.getCount() == 1) {
			return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/admin/details/" + name).build()).build();
		}

		accountStore.remove(accountStore.get(name));

		return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/admin/list/10/1").build()).build();
	}

	/**
	 * Shows a form to create a new admin account.
	 *
	 * @return the form
	 */
	@GET
	@Path(value = "create")
	@Produces("text/html")
	@Template(name = "/admin/create.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showCreateForm() {
		return new TreeMap<>();
	}

	/**
	 * Create new admin account.
	 *
	 * @return redirect
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path(value = "create")
	@RequireBackofficeAccount
	public Response doCreate(@FormParam("login_name") String name, @FormParam("login_password") String password, @FormParam("login_password_repeat") String passwordRepeat) {
		// Check for fail
		if (name.equals("") || !password.equals(passwordRepeat)) {
			return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/admin/create").build()).build();
		}

		// Create store entity from persistence context
		BackofficeAccountStore accountStore = new BackofficeAccountStore(em);
		// Now get the account the user tries to get details of
		BackofficeAccount account = new BackofficeAccount(name, password);

		try {
			accountStore.add(account);
		} catch (RollbackException e) {
			return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/admin/create").build()).build();
		}

		return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/admin/details/" + name).build()).build();
	}

	/**
	 * Shows a form to change an admin account's password.
	 *
	 * @return the form
	 */
	@GET
	@Path(value = "changePw/{name}")
	@Produces("text/html")
	@Template(name = "/admin/changePw.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showChangePwForm(@PathParam("name") String name) {
		Map<String, Object> result = new TreeMap<>();
		result.put("name", name);
		return result;
	}

	/**
	 * Change admin account password.
	 *
	 * @return redirect
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path(value = "changePw")
	@RequireBackofficeAccount
	public Response doChangePw(@FormParam("login_name") String name, @FormParam("new_password") String newPassword, @FormParam("new_password_repeat") String newPasswordRepeat) {
		// Check for fail
		if (name.equals("") || !newPassword.equals(newPasswordRepeat)) {
			return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/admin/changePw/" + name).build()).build();
		}

		// Create store entity from persistence context
		BackofficeAccountStore accountStore = new BackofficeAccountStore(em);
		// Now get the account the user tries to edit
		BackofficeAccount account = accountStore.get(name);

		account.changePassword(newPassword);

		try {
			accountStore.update(account);
		} catch (RollbackException e) {
			return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/admin/changePw/" + name).build()).build();
		}

		return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/admin/details/" + name).build()).build();
	}
}
