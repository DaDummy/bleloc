package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedUserAccount;
import edu.kit.informatik.pse.bleloc.annotations.RequireUserAccount;
import edu.kit.informatik.pse.bleloc.cdi.UserAccountProxy;
import edu.kit.informatik.pse.bleloc.model.*;
import edu.kit.informatik.pse.bleloc.payload.DeviceHashTablePayload;
import edu.kit.informatik.pse.bleloc.payload.DeviceTrackingResultPayload;

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
 * Handles requests regarding device tracking
 */
@Path(value = "scan")
@Stateless
public class DeviceTracking {

	@Inject
	@AuthenticatedUserAccount
	UserAccountProxy accountProxy;

	@PersistenceContext
	EntityManager em;

	@Inject
	DeviceHashTableStore deviceHashTableStore;

	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "filter")
	public Response getFilter() {
		DeviceHashTable deviceHashTable = deviceHashTableStore.get();

		DeviceHashTablePayload deviceHashTablePayload = new DeviceHashTablePayload();
		deviceHashTablePayload.setData(deviceHashTable.getSerialized());
		deviceHashTablePayload.setModifiedAt(deviceHashTable.getLastUpdateTime());
		return Response.ok(deviceHashTablePayload).build();
	}

	@RequireUserAccount
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path("result/{n}")
	public Response getResult(@PathParam("n") int n) {
		UserAccount account = accountProxy.getAccount();

		TrackingResultStore trackingResultStore = new TrackingResultStore(em);

		TrackingResult trackingResult = trackingResultStore.getNthOldestByUserAccount(account, n);

		if (trackingResult != null) {
			DeviceTrackingResultPayload deviceTrackingResultPayload = new DeviceTrackingResultPayload();
			deviceTrackingResultPayload.setTrackingResultId(trackingResult.getId());
			deviceTrackingResultPayload.setHashedHardwareIdentifier(trackingResult.getDevice().getHardwareIdentifier().toString());
			deviceTrackingResultPayload.setEncounteredAt(trackingResult.getEncounteredAt());
			deviceTrackingResultPayload.setEncryptedData(trackingResult.getEncryptedData());
			return Response.ok(deviceTrackingResultPayload).build();
		}

		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@RequireUserAccount
	@POST
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "resultReceived/{scanResultId}")
	public Response postResultReceived(@PathParam("scanResultId") long scanResultId) {
		UserAccount account = accountProxy.getAccount();

		TrackingResultStore trackingResultStore = new TrackingResultStore(em);

		TrackingResult trackingResult = trackingResultStore.get(scanResultId);

		if (trackingResult != null && trackingResult.getUserAccount().getId().equals(account.getId())) {
			trackingResultStore.remove(trackingResult);

			return Response.ok().build();
		}

		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "result")
	public Response postResult(DeviceTrackingResultPayload result) {
		DeviceStore deviceStore = new DeviceStore(em);
		TrackingResultStore trackingResultStore = new TrackingResultStore(em);

		Collection<Device> devices = deviceStore.getByHardwareIdentifer(HashedMacAddress.fromString(result.getHashedHardwareIdentifier()));
		for (Device device : devices) {
			TrackingResult trackingResult = new TrackingResult(device, result.getEncounteredAt(), result.getEncryptedData());
			trackingResultStore.add(trackingResult);
		}

		return Response.ok().build();
	}
}
