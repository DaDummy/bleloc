package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.model.*;
import edu.kit.informatik.pse.bleloc.requestfilters.RequireBackofficeAccountFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.*;

public class AuthenticationBackofficeTest extends BackofficeTest {
	private static Authentication authentication;

	public AuthenticationBackofficeTest() {
		authentication = new Authentication();
		injectObjectsInto(authentication);

		em.getTransaction().begin();
		BackofficeAccountStore userStore = new BackofficeAccountStore(em);
		userStore.add(new BackofficeAccount("correct", "correct"));
		em.getTransaction().commit();
	}

	private static void assertRedirect(Response r, String path) {
		Assert.assertEquals(303, r.getStatus());
		Assert.assertEquals(path, r.getLocation().getPath());
	}

	@Before
	public void setUp() {
		em.getTransaction().begin();
	}

	@After
	public void tearDown() {
		em.getTransaction().rollback();
	}

	@Test
	public void testShowLoginForm() {
		Assert.assertNotNull(authentication.showLoginForm());
	}

	@Test
	public void testDoLoginBadUser() {
		assertRedirect(authentication.doLogin("wrong", "wrong"), "auth");
	}

	@Test
	public void testDoLoginBadPassword() {
		assertRedirect(authentication.doLogin("correct", "wrong"), "auth");
	}

	@Test
	public void testDoLoginCorrect() {
		assertRedirect(authentication.doLogin("correct", "correct"), "");
	}

	@Test
	public void testDoLogout() {
		assertRedirect(authentication.doLogout(new HttpHeaders() {
			@Override
			public List<String> getRequestHeader(String name) {
				return null;
			}

			@Override
			public String getHeaderString(String name) {
				return null;
			}

			@Override
			public MultivaluedMap<String, String> getRequestHeaders() {
				return null;
			}

			@Override
			public List<MediaType> getAcceptableMediaTypes() {
				return null;
			}

			@Override
			public List<Locale> getAcceptableLanguages() {
				return null;
			}

			@Override
			public MediaType getMediaType() {
				return null;
			}

			@Override
			public Locale getLanguage() {
				return null;
			}

			@Override
			public Map<String, Cookie> getCookies() {
				Map<String, Cookie> map = new HashMap<>();
				map.put(RequireBackofficeAccountFilter.CookieName, new Cookie(RequireBackofficeAccountFilter.CookieName, "_"));
				return map;
			}

			@Override
			public Date getDate() {
				return null;
			}

			@Override
			public int getLength() {
				return 0;
			}
		}), "auth");
	}
}
