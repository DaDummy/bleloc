package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedUserAccount;
import edu.kit.informatik.pse.bleloc.annotations.RequireUserAccount;
import edu.kit.informatik.pse.bleloc.cdi.UserAccountProxy;
import edu.kit.informatik.pse.bleloc.model.UserAccount;
import edu.kit.informatik.pse.bleloc.model.UserDataEntry;
import edu.kit.informatik.pse.bleloc.model.UserDataStore;
import edu.kit.informatik.pse.bleloc.payload.UserDataIndexEntry;
import edu.kit.informatik.pse.bleloc.payload.UserDataIndexPayload;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Handles requests regarding user data synchronization
 */
@Path(value = "user/sync")
@Stateless
public class UserDataSynchronization {

	@Inject
	@AuthenticatedUserAccount
	UserAccountProxy accountProxy;

	@PersistenceContext
	EntityManager em;

	@RequireUserAccount
	@POST
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "addData")
	public Response postAddData(UserDataPayload syncData) {
		UserAccount account = accountProxy.getAccount();

		UserDataStore userDataStore = new UserDataStore(em);

		UserDataEntry userDataEntry = new UserDataEntry(syncData.getModifiedAt(), syncData.getEncryptedData(), account);

		userDataStore.add(userDataEntry);

		syncData.setSyncId(userDataEntry.getId());

		return Response.ok(syncData).build();
	}

	@RequireUserAccount
	@PUT
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "data/{identifier}")
	public Response postUpdateData(UserDataPayload syncData) {
		UserAccount account = accountProxy.getAccount();

		UserDataStore userDataStore = new UserDataStore(em);

		UserDataEntry userDataEntry = userDataStore.get(account, syncData.getSyncId());

		if (userDataEntry != null) {
			if (syncData.getModifiedAt().after(userDataEntry.getModifiedAt())) {
				userDataEntry.setModifiedAt(syncData.getModifiedAt());
				userDataEntry.setEncryptedData(syncData.getEncryptedData());
				userDataStore.update(userDataEntry);
			}

			UserDataPayload userDataPayload = new UserDataPayload();
			userDataPayload.setSyncId(userDataEntry.getId());
			userDataPayload.setModifiedAt(userDataEntry.getModifiedAt());
			userDataPayload.setEncryptedData(userDataEntry.getEncryptedData());

			return Response.ok(userDataPayload).build();
		}

		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@RequireUserAccount
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "data/{identifier}")
	public Response getData(@PathParam("identifier") long identifier) {
		UserAccount account = accountProxy.getAccount();

		UserDataStore userDataStore = new UserDataStore(em);

		UserDataEntry userDataEntry = userDataStore.get(account, identifier);

		if (userDataEntry != null) {
			UserDataPayload userDataPayload = new UserDataPayload();
			userDataPayload.setSyncId(userDataEntry.getId());
			userDataPayload.setModifiedAt(userDataEntry.getModifiedAt());
			userDataPayload.setEncryptedData(userDataEntry.getEncryptedData());

			return Response.ok(userDataPayload).build();
		}

		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@RequireUserAccount
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "data")
	public Response getDataIndex(@QueryParam("changedAfter") Long changedAfter) {
		UserAccount account = accountProxy.getAccount();

		UserDataStore userDataStore = new UserDataStore(em);

		Collection<UserDataIndexEntry> userDataIndex = userDataStore.getIndexByUserAccount(account);

		UserDataIndexPayload userDataIndexPayload = new UserDataIndexPayload();
		userDataIndexPayload.setIndex(userDataIndex);
		return Response.ok(userDataIndexPayload).build();
	}
}
