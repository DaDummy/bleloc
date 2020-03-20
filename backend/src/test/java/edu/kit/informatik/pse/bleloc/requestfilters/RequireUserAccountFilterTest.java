package edu.kit.informatik.pse.bleloc.requestfilters;

import edu.kit.informatik.pse.bleloc.backend.controller.ControllerTest;
import edu.kit.informatik.pse.bleloc.cdi.UserAccountProxy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.util.TypeLiteral;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.CompletionStage;

public class RequireUserAccountFilterTest extends ControllerTest {
	RequireUserAccountFilter filter;
	UserAccountProxy firedAccountProxy;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Create instance and emulate dependency injection
		filter = new RequireUserAccountFilter();
		filter.em = em;
		// Create a mock instance
		filter.userAccountAuthenticatedEvent = new Event<UserAccountProxy>() {
			@Override
			public void fire(UserAccountProxy accountProxy) {
				firedAccountProxy = accountProxy;
			}

			@Override
			public <U extends UserAccountProxy> CompletionStage<U> fireAsync(U u) {
				return null;
			}

			@Override
			public <U extends UserAccountProxy> CompletionStage<U> fireAsync(U u,
			                                                                 NotificationOptions notificationOptions) {
				return null;
			}

			@Override
			public Event<UserAccountProxy> select(Annotation... annotations) {
				return null;
			}

			@Override
			public <U extends UserAccountProxy> Event<U> select(Class<U> aClass, Annotation... annotations) {
				return null;
			}

			@Override
			public <U extends UserAccountProxy> Event<U> select(TypeLiteral<U> typeLiteral, Annotation... annotations) {
				return null;
			}
		};
	}

	@Test
	public void filterSuccess() throws IOException {
		ContainerRequestContext context = generateContainerRequestContextMock(userAccount.getId());

		filter.filter(context);

		Response response = getAbortedWithResponse();
		Assert.assertNull(response);
		Assert.assertNotNull(firedAccountProxy);
		Assert.assertEquals(userAccount, firedAccountProxy.getAccount());
	}

	@Test
	public void filterFail() throws IOException {
		ContainerRequestContext context = generateContainerRequestContextMock(null);

		filter.filter(context);

		Response response = getAbortedWithResponse();
		Assert.assertNotNull(response);
		Assert.assertNull(firedAccountProxy);
		Assert.assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
	}
}