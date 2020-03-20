package edu.kit.informatik.pse.bleloc.client.model.user;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class AuthenticationResultTest {

	@Test
	public void SUCCESSTest(){
		Assert.assertThat(AuthenticationResult.valueOf("SUCCESS"),is(notNullValue()));
	}

	@Test
	public void INVALID_REQUESTTest(){
		Assert.assertThat(AuthenticationResult.valueOf("INVALID_REQUEST"),is(notNullValue()));
	}

	@Test
	public void SERVER_BUSYTest(){
		Assert.assertThat(AuthenticationResult.valueOf("SERVER_BUSY"),is(notNullValue()));
	}

	@Test
	public void NETWORK_ERRORTest(){
		Assert.assertThat(AuthenticationResult.valueOf("NETWORK_ERROR"),is(notNullValue()));
	}

	@Test
	public void USERNAME_TAKENTest(){
		Assert.assertThat(AuthenticationResult.valueOf("USERNAME_TAKEN"),is(notNullValue()));
	}
}
