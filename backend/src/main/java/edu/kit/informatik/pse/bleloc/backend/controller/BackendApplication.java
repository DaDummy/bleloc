package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.requestfilters.RequireUserAccountFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath(value = "/backend")
public class BackendApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>();

		classes.add(DeviceManagement.class);
		classes.add(DeviceTracking.class);
		classes.add(UserAccountManagement.class);
		classes.add(UserDataSynchronization.class);
		classes.add(RequireUserAccountFilter.class);
		classes.add(JacksonFeature.class);

		return classes;
	}
}
