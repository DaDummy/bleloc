package edu.kit.informatik.pse.bleloc.backoffice;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class BackofficeApplicationTest {

	@Test
	public void getClasses() {
		BackofficeApplication app = new BackofficeApplication();
		Set<Class<?>> classes = app.getClasses();
		Assert.assertNotNull(classes);
		for (Class<?> aClass : classes) {
			Assert.assertNotNull(aClass);
		}
	}
}