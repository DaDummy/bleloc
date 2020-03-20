package edu.kit.informatik.pse.bleloc.backoffice;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;

import javax.ws.rs.ApplicationPath;

import edu.kit.informatik.pse.bleloc.cdi.AuthenticatedBackofficeAccountProducer;
import edu.kit.informatik.pse.bleloc.requestfilters.RequireBackofficeAccountFilter;

@ApplicationPath("/backoffice")
public class BackofficeApplication extends ResourceConfig {

	public BackofficeApplication() {
		packages("com.baeldung.jersey.server");
		property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "templates");
		register(FreemarkerMvcFeature.class);
		register(Authentication.class);
		register(Home.class);
		register(UserAccountManagement.class);
		register(BackofficeAccountManagement.class);
		register(Statistics.class);
		register(Settings.class);
		register(RequireBackofficeAccountFilter.class);
	}
}
