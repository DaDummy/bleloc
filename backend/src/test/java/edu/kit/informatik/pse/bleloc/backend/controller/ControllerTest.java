package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.cdi.UserAccountProxy;
import edu.kit.informatik.pse.bleloc.model.*;
import edu.kit.informatik.pse.bleloc.requestfilters.RequireUserAccountFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.util.*;

public abstract class ControllerTest {
	public static final String UserName = "test123";
	public static final String UserPassword = "foobar";
	public static final long NumberOfAccounts = 1;

	public static final String DeviceAddressString = "123456789a";
	public static final HashedMacAddress DeviceAddress = HashedMacAddress.fromString(DeviceAddressString);
	public static final long NumberOfDevices = 1;

	public static final Date UserDataModifiedAt = new Date();
	public static final byte[] UserDataEncryptedData = new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};

	public static final Date TrackingResultEncounteredAt = new Date();
	public static final byte[] TrackingResultEncryptedData =
		new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef, 0x01};

	private static final String ContextPath = "/backend";

	protected EntityManager em;

	protected UserAccount userAccount;
	protected Device device;
	protected UserDataEntry userDataEntry;
	protected TrackingResult trackingResult;

	protected DeviceHashTableStore deviceHashTableStore;

	protected UserAccountStore userAccountStore;
	protected DeviceStore deviceStore;
	protected UserDataStore userDataStore;
	protected TrackingResultStore trackingResultStore;

	@Before
	public void setUp() throws Exception {
		Assert.assertNull(em);

		em = createEntityManagerForTest();

		deviceHashTableStore = new DeviceHashTableStore();

		em.getTransaction().begin();
		userAccount = new UserAccount(UserName, UserPassword);
		userAccountStore = new UserAccountStore(em);
		userAccountStore.add(userAccount);
		em.getTransaction().commit();
		em.detach(userAccount);

		em.getTransaction().begin();
		device = new Device(userAccount, DeviceAddress);
		deviceStore = new DeviceStore(em);
		deviceStore.add(device);
		em.getTransaction().commit();
		em.detach(device);

		em.getTransaction().begin();
		userDataEntry = new UserDataEntry(UserDataModifiedAt, UserDataEncryptedData, userAccount);
		userDataStore = new UserDataStore(em);
		userDataStore.add(userDataEntry);
		em.getTransaction().commit();
		em.detach(userDataEntry);

		em.getTransaction().begin();
		trackingResult = new TrackingResult(device, TrackingResultEncounteredAt, TrackingResultEncryptedData);
		trackingResultStore = new TrackingResultStore(em);
		trackingResultStore.add(trackingResult);
		em.getTransaction().commit();
		em.detach(trackingResult);

		em.getTransaction().begin();
		DeviceHashTableManager manager = new DeviceHashTableManager(deviceStore, deviceHashTableStore);
		manager.recreateDeviceHashTable();
		em.getTransaction().commit();

		em.getTransaction().begin();
	}

	@After
	public void tearDown() throws Exception {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		em.close();
	}

	protected EntityManager createEntityManagerForTest() {
		return Persistence.createEntityManagerFactory("testDB").createEntityManager();
	}

	private Cookie generateLoginCookie(Long accountId) {
		// Create the login cookie
		String cookieString = RequireUserAccountFilter.ActiveAuthenticator.createCookie(accountId);

		return new Cookie(RequireUserAccountFilter.CookieName, cookieString);
	}

	protected UserAccountProxy generateAccountProxy() {
		return new UserAccountProxy(userAccount);
	}

	protected HttpServletRequest generateFakeRequest() {
		return new HttpServletRequest() {
			@Override
			public String getAuthType() {
				return null;
			}

			@Override
			public javax.servlet.http.Cookie[] getCookies() {
				return new javax.servlet.http.Cookie[0];
			}

			@Override
			public long getDateHeader(String s) {
				return 0;
			}

			@Override
			public String getHeader(String s) {
				return null;
			}

			@Override
			public Enumeration<String> getHeaders(String s) {
				return null;
			}

			@Override
			public Enumeration<String> getHeaderNames() {
				return null;
			}

			@Override
			public int getIntHeader(String s) {
				return 0;
			}

			@Override
			public String getMethod() {
				return null;
			}

			@Override
			public String getPathInfo() {
				return null;
			}

			@Override
			public String getPathTranslated() {
				return null;
			}

			@Override
			public String getContextPath() {
				return ContextPath;
			}

			@Override
			public String getQueryString() {
				return null;
			}

			@Override
			public String getRemoteUser() {
				return null;
			}

			@Override
			public boolean isUserInRole(String s) {
				return false;
			}

			@Override
			public Principal getUserPrincipal() {
				return null;
			}

			@Override
			public String getRequestedSessionId() {
				return null;
			}

			@Override
			public String getRequestURI() {
				return null;
			}

			@Override
			public StringBuffer getRequestURL() {
				return null;
			}

			@Override
			public String getServletPath() {
				return null;
			}

			@Override
			public HttpSession getSession(boolean b) {
				return null;
			}

			@Override
			public HttpSession getSession() {
				return null;
			}

			@Override
			public String changeSessionId() {
				return null;
			}

			@Override
			public boolean isRequestedSessionIdValid() {
				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromCookie() {
				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromURL() {
				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromUrl() {
				return false;
			}

			@Override
			public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
				return false;
			}

			@Override
			public void login(String s, String s1) throws ServletException {

			}

			@Override
			public void logout() throws ServletException {

			}

			@Override
			public Collection<Part> getParts() throws IOException, ServletException {
				return null;
			}

			@Override
			public Part getPart(String s) throws IOException, ServletException {
				return null;
			}

			@Override
			public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
				return null;
			}

			@Override
			public Object getAttribute(String s) {
				return null;
			}

			@Override
			public Enumeration<String> getAttributeNames() {
				return null;
			}

			@Override
			public String getCharacterEncoding() {
				return null;
			}

			@Override
			public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

			}

			@Override
			public int getContentLength() {
				return 0;
			}

			@Override
			public long getContentLengthLong() {
				return 0;
			}

			@Override
			public String getContentType() {
				return null;
			}

			@Override
			public ServletInputStream getInputStream() throws IOException {
				return null;
			}

			@Override
			public String getParameter(String s) {
				return null;
			}

			@Override
			public Enumeration<String> getParameterNames() {
				return null;
			}

			@Override
			public String[] getParameterValues(String s) {
				return new String[0];
			}

			@Override
			public Map<String, String[]> getParameterMap() {
				return null;
			}

			@Override
			public String getProtocol() {
				return null;
			}

			@Override
			public String getScheme() {
				return null;
			}

			@Override
			public String getServerName() {
				return null;
			}

			@Override
			public int getServerPort() {
				return 0;
			}

			@Override
			public BufferedReader getReader() throws IOException {
				return null;
			}

			@Override
			public String getRemoteAddr() {
				return null;
			}

			@Override
			public String getRemoteHost() {
				return null;
			}

			@Override
			public void setAttribute(String s, Object o) {

			}

			@Override
			public void removeAttribute(String s) {

			}

			@Override
			public Locale getLocale() {
				return null;
			}

			@Override
			public Enumeration<Locale> getLocales() {
				return null;
			}

			@Override
			public boolean isSecure() {
				return false;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String s) {
				return null;
			}

			@Override
			public String getRealPath(String s) {
				return null;
			}

			@Override
			public int getRemotePort() {
				return 0;
			}

			@Override
			public String getLocalName() {
				return null;
			}

			@Override
			public String getLocalAddr() {
				return null;
			}

			@Override
			public int getLocalPort() {
				return 0;
			}

			@Override
			public ServletContext getServletContext() {
				return null;
			}

			@Override
			public AsyncContext startAsync() throws IllegalStateException {
				return null;
			}

			@Override
			public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
				throws IllegalStateException {
				return null;
			}

			@Override
			public boolean isAsyncStarted() {
				return false;
			}

			@Override
			public boolean isAsyncSupported() {
				return false;
			}

			@Override
			public AsyncContext getAsyncContext() {
				return null;
			}

			@Override
			public DispatcherType getDispatcherType() {
				return null;
			}
		};
	}

	private Response abortedWithResponse;

	protected ContainerRequestContext generateContainerRequestContextMock(Long accountId) {
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

	protected Response getAbortedWithResponse() {
		return abortedWithResponse;
	}

	protected UserTransaction generateUserTransaction() {
		return  new UserTransaction() {
			@Override
			public void begin() throws NotSupportedException, SystemException {
				em.getTransaction().begin();
			}

			@Override
			public void commit()
				throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException,
				       IllegalStateException, SystemException {
				em.getTransaction().commit();
			}

			@Override
			public void rollback() throws IllegalStateException, SecurityException, SystemException {
				em.getTransaction().rollback();
			}

			@Override
			public void setRollbackOnly() throws IllegalStateException, SystemException {
				em.getTransaction().setRollbackOnly();
			}

			@Override
			public int getStatus() throws SystemException {
				return 0;
			}

			@Override
			public void setTransactionTimeout(int i) throws SystemException {

			}
		};
	}
}
