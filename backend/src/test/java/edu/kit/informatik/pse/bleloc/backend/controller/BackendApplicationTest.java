package edu.kit.informatik.pse.bleloc.backend.controller;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class BackendApplicationTest {

	@Test
	public void getClasses() {
		BackendApplication app = new BackendApplication();
		Set<Class<?>> classes = app.getClasses();
		Assert.assertNotNull(classes);
		for (Class<?> aClass : classes) {
			Assert.assertNotNull(aClass);
		}
	}
}