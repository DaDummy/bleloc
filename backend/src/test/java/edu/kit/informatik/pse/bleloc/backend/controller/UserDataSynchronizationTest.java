package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.payload.UserDataIndexEntry;
import edu.kit.informatik.pse.bleloc.payload.UserDataIndexPayload;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;

public class UserDataSynchronizationTest extends ControllerTest {
	private static final Date NewUserDataModifiedAt = Date.from(UserDataModifiedAt.toInstant().plusSeconds(5));
	private static final byte[] NewUserDataEncryptedData =
		new byte[]{(byte) 0xfe, (byte) 0xdc, (byte) 0xba, (byte) 0x98};

	private UserDataSynchronization resource;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		resource = new UserDataSynchronization();
		resource.em = em;
		resource.accountProxy = generateAccountProxy();
	}

	@Test
	public void postAddData() {
		UserDataPayload payload = new UserDataPayload();
		payload.setModifiedAt(NewUserDataModifiedAt);
		payload.setEncryptedData(NewUserDataEncryptedData);

		Response response = resource.postAddData(payload);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		UserDataPayload responsePayload = (UserDataPayload) response.getEntity();
		Assert.assertNotNull(responsePayload);

		payload.setSyncId(responsePayload.getSyncId());
		Assert.assertEquals(payload, responsePayload);
	}

	@Test
	public void postUpdateDataOkNewer() {
		UserDataPayload payload = new UserDataPayload();
		payload.setSyncId(userDataEntry.getId());
		payload.setModifiedAt(Date.from(userDataEntry.getModifiedAt().toInstant().plusSeconds(10)));
		payload.setEncryptedData(NewUserDataEncryptedData);

		Response response = resource.postUpdateData(payload);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		UserDataPayload responsePayload = (UserDataPayload) response.getEntity();
		Assert.assertNotNull(responsePayload);

		Assert.assertEquals(payload, responsePayload);
	}

	@Test
	public void postUpdateDataOkOlder() {
		UserDataPayload payload = new UserDataPayload();
		payload.setSyncId(userDataEntry.getId());
		payload.setModifiedAt(Date.from(UserDataModifiedAt.toInstant().minusSeconds(10)));
		payload.setEncryptedData(NewUserDataEncryptedData);

		Response response = resource.postUpdateData(payload);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		UserDataPayload responsePayload = (UserDataPayload) response.getEntity();
		Assert.assertNotNull(responsePayload);

		payload.setModifiedAt(UserDataModifiedAt);
		payload.setEncryptedData(UserDataEncryptedData);
		Assert.assertEquals(payload, responsePayload);
	}

	@Test
	public void postUpdateDataNotFound() {
		final long syncId = userDataEntry.getId() + 100;

		UserDataPayload payload = new UserDataPayload();
		payload.setSyncId(syncId);
		payload.setModifiedAt(Date.from(UserDataModifiedAt.toInstant().plusSeconds(10)));
		payload.setEncryptedData(NewUserDataEncryptedData);

		Response response = resource.postUpdateData(payload);

		Assert.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void getDataOk() {
		final long syncId = userDataEntry.getId();
		Response response = resource.getData(syncId);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		UserDataPayload responsePayload = (UserDataPayload) response.getEntity();
		Assert.assertNotNull(responsePayload);

		UserDataPayload payload = new UserDataPayload();
		payload.setSyncId(syncId);
		payload.setModifiedAt(UserDataModifiedAt);
		payload.setEncryptedData(UserDataEncryptedData);
		Assert.assertEquals(payload, responsePayload);
	}

	@Test
	public void getDataNotFound() {
		final long syncId = userDataEntry.getId() + 100;
		Response response = resource.getData(syncId);

		Assert.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void getDataIndexAll() {
		// changedAfter parameter is currently unsupported and unused and as such does not require testing
		Response response = resource.getDataIndex(null);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		UserDataIndexPayload responsePayload = (UserDataIndexPayload) response.getEntity();
		Assert.assertNotNull(responsePayload);

		UserDataIndexEntry entry = new UserDataIndexEntry(userDataEntry.getId(), userDataEntry.getModifiedAt());
		UserDataIndexPayload payload = new UserDataIndexPayload();
		ArrayList<UserDataIndexEntry> index = new ArrayList<>();
		index.add(entry);
		payload.setIndex(index);
		Assert.assertEquals(payload, responsePayload);
	}
}