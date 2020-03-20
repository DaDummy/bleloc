package edu.kit.informatik.pse.bleloc.backoffice;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedBackofficeAccount;
import edu.kit.informatik.pse.bleloc.annotations.RequireBackofficeAccount;
import edu.kit.informatik.pse.bleloc.cdi.BackofficeAccountProxy;
import org.glassfish.jersey.server.mvc.Template;

import java.util.Map;
import java.util.TreeMap;

@Path("")
@Stateless
public class Home {
	@Inject
	@AuthenticatedBackofficeAccount
	private BackofficeAccountProxy accountProxy;

	/**
	 * Shows a static menu with choices like Administrators, Users, Statistics, Settings, and Logout.
	 */
	@GET
	@Produces("text/html")
	@Template(name = "/menu.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showPage() {
		return new TreeMap<>();
	}
}
