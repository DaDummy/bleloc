package edu.kit.informatik.pse.bleloc.backoffice;

import org.junit.Assert;
import org.junit.Test;

public class HomeTest {
	private Home home;

	public HomeTest() {
		home = new Home();
	}

	@Test
	public void testShowPage() {
		Assert.assertNotNull(home.showPage());
	}
}
