package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.annotations.RequireBackofficeAccount;
import edu.kit.informatik.pse.bleloc.model.UserAccount;
import edu.kit.informatik.pse.bleloc.model.UserAccountStore;
import org.glassfish.jersey.server.mvc.Template;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Path(value = "user")
@Stateless
public class UserAccountManagement {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Shows user accounts.
	 *
	 * @param page
	 * 		the number of the page shown
	 * @return the list of accounts
	 */
	@GET
	@Path(value = "list/{size}/{page}")
	@Produces("text/html")
	@Template(name = "/user/list.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showList(@PathParam(value = "size") int size, @PathParam(value = "page") int page) {
		// Variables for FTL
		Map<String, Object> result = new TreeMap<>();

		// Create store entity from persistence context
		UserAccountStore accountStore = new UserAccountStore(em);
		// Get get the accounts to be displayed
		List<UserAccount> accounts = accountStore.list(size, page);
		result.put("accounts", accounts);

		result.put("size", size);
		result.put("page", page);
		result.put("pageCount", (accountStore.getCount() + size - 1) / size);

		return result;
	}

	/**
	 * Shows details abount a user account.
	 *
	 * @param id
	 * 		the user id of the account
	 * @return account details
	 */
	@GET
	@Path(value = "details/{id}")
	@Produces("text/html")
	@Template(name = "/user/details.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showDetails(@PathParam(value = "id") long id) {
		// Create store entity from persistence context
		UserAccountStore accountStore = new UserAccountStore(em);
		// Now get the account the user tries to get details of
		UserAccount account = accountStore.get(id);

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
	 * Deletes a user account.
	 *
	 * @param id
	 * 		the id of the account
	 * @return redirect to account list
	 */
	@POST
	@Path(value = "delete/{id}")
	@RequireBackofficeAccount
	public Response doDelete(@PathParam(value = "id") long id) {
		// Create store entity from persistence context
		UserAccountStore accountStore = new UserAccountStore(em);

		accountStore.remove(accountStore.get(id));

		return Response.seeOther(UriBuilder.fromPath("/backoffice/backoffice/user/list/10/1").build()).build();
	}
}
