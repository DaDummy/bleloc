package edu.kit.informatik.pse.bleloc.requestfilters;

import edu.kit.informatik.pse.bleloc.backoffice.Authentication;
import edu.kit.informatik.pse.bleloc.backoffice.BackofficeTest;
import edu.kit.informatik.pse.bleloc.cdi.BackofficeAccountProxy;
import edu.kit.informatik.pse.bleloc.model.BackofficeAccount;
import edu.kit.informatik.pse.bleloc.model.BackofficeAccountStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.util.TypeLiteral;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletionStage;

public class RequireBackofficeAccountFilterTest extends BackofficeTest {
	private static final String UserName = "test123";
	private static final String UserPassword = "foobar";

	RequireBackofficeAccountFilter filter;
	BackofficeAccountProxy firedAccountProxy;

	@Before
	public void setUp() throws Exception {
		// Create instance and emulate dependency injection
		filter = new RequireBackofficeAccountFilter();

		injectObjectsInto(filter);

		// Create a mock instance
		filter.backofficeAccountAuthenticatedEvent = new Event<BackofficeAccountProxy>() {
			@Override
			public void fire(BackofficeAccountProxy accountProxy) {
				firedAccountProxy = accountProxy;
			}

			@Override
			public <U extends BackofficeAccountProxy> CompletionStage<U> fireAsync(U u) {
				return null;
			}

			@Override
			public <U extends BackofficeAccountProxy> CompletionStage<U> fireAsync(U u,
			                                                                       NotificationOptions notificationOptions) {
				return null;
			}

			@Override
			public Event<BackofficeAccountProxy> select(Annotation... annotations) {
				return null;
			}

			@Override
			public <U extends BackofficeAccountProxy> Event<U> select(Class<U> aClass, Annotation... annotations) {
				return null;
			}

			@Override
			public <U extends BackofficeAccountProxy> Event<U> select(TypeLiteral<U> typeLiteral,
			                                                          Annotation... annotations) {
				return null;
			}
		};
	}

	@Test
	public void filterSuccess() throws IOException {
		em.getTransaction().begin();
		BackofficeAccount backofficeAccount = new BackofficeAccount(UserName, UserPassword);

		BackofficeAccountStore backofficeAccountStore = new BackofficeAccountStore(em);
		backofficeAccountStore.add(backofficeAccount);
		em.getTransaction().commit();
		em.detach(backofficeAccount);

		ContainerRequestContext context = generateContainerRequestContextMock(backofficeAccount.getId());

		filter.filter(context);

		Response response = getAbortedWithResponse();
		Assert.assertNull(response);
		Assert.assertNotNull(firedAccountProxy);
		Assert.assertEquals(backofficeAccount, firedAccountProxy.getAccount());
	}

	@Test
	public void filterFail() throws IOException {
		ContainerRequestContext context = generateContainerRequestContextMock(null);

		filter.filter(context);

		Response response = getAbortedWithResponse();
		Assert.assertNotNull(response);
		Assert.assertNull(firedAccountProxy);
		Assert.assertEquals(Response.Status.SEE_OTHER, response.getStatusInfo());
		Assert.assertEquals(UriBuilder.fromResource(Authentication.class).build(), response.getLocation());
	}

	private Response abortedWithResponse;

	private Cookie generateLoginCookie(Long accountId) {
		// Create the login cookie
		String cookieString = RequireBackofficeAccountFilter.ActiveAuthenticator.createCookie(accountId);

		return new Cookie(RequireBackofficeAccountFilter.CookieName, cookieString);
	}

	private ContainerRequestContext generateContainerRequestContextMock(Long accountId) {
		Map<String, Cookie> cookies = new TreeMap<>();

		if (accountId != null) {
			Cookie cookie = generateLoginCookie(accountId);
			cookies.put(cookie.getName(), cookie);
		}

		return new ContainerRequestContext() {
			@Override
			public Object getProperty(String s) {
				return null;
			}

			@Override
			public Collection<String> getPropertyNames() {
				return null;
			}

			@Override
			public void setProperty(String s, Object o) {

			}

			@Override
			public void removeProperty(String s) {

			}

			@Override
			public UriInfo getUriInfo() {
				return null;
			}

			@Override
			public void setRequestUri(URI uri) {

			}

			@Override
			public void setRequestUri(URI uri, URI uri1) {

			}

			@Override
			public Request getRequest() {
				return null;
			}

			@Override
			public String getMethod() {
				return null;
			}

			@Override
			public void setMethod(String s) {

			}

			@Override
			public MultivaluedMap<String, String> getHeaders() {
				return null;
			}

			@Override
			public String getHeaderString(String s) {
				return null;
			}

			@Override
			public Date getDate() {
				return null;
			}

			@Override
			public Locale getLanguage() {
				return null;
			}

			@Override
			public int getLength() {
				return 0;
			}

			@Override
			public MediaType getMediaType() {
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
			public Map<String, Cookie> getCookies() {
				return cookies;
			}

			@Override
			public boolean hasEntity() {
				return false;
			}

			@Override
			public InputStream getEntityStream() {
				return null;
			}

			@Override
			public void setEntityStream(InputStream inputStream) {

			}

			@Override
			public SecurityContext getSecurityContext() {
				return null;
			}

			@Override
			public void setSecurityContext(SecurityContext securityContext) {

			}

			@Override
			public void abortWith(Response response) {
				abortedWithResponse = response;
			}
		};
	}

	private Response getAbortedWithResponse() {
		return abortedWithResponse;
	}
}