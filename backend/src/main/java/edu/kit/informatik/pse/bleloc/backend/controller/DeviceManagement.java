package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.annotations.AuthenticatedUserAccount;
import edu.kit.informatik.pse.bleloc.annotations.RequireUserAccount;
import edu.kit.informatik.pse.bleloc.cdi.UserAccountProxy;
import edu.kit.informatik.pse.bleloc.model.*;
import edu.kit.informatik.pse.bleloc.payload.DeviceIndexPayload;
import edu.kit.informatik.pse.bleloc.payload.DevicePayload;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Handles requests regarding device registration and information
 */
@Path(value = "device")
@Stateless
public class DeviceManagement {

	@Inject
	@AuthenticatedUserAccount
	UserAccountProxy accountProxy;

	@PersistenceContext
	EntityManager em;

	@Inject
	DeviceHashTableStore deviceHashTableStore;

	@RequireUserAccount
	@POST
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "register")
	public Response postRegister(edu.kit.informatik.pse.bleloc.payload.Device payload) {
		UserAccount account = accountProxy.getAccount();

		DeviceStore deviceStore = new DeviceStore(em);

		// Create a DeviceHashTableManager and register it as a listener, so the DeviceHashTable gets updated if this request causes a database change.
		DeviceHashTableManager manager = new DeviceHashTableManager(deviceStore, deviceHashTableStore);
		deviceStore.registerDeviceStoreListener(manager);

		Device device = new Device(account, HashedMacAddress.fromString(payload.getHardwareIdentifier()));

		deviceStore.add(device);

		DevicePayload devicePayload = new DevicePayload();
		devicePayload.setHashedHardwareIdentifier(device.getHardwareIdentifier().toString());
		devicePayload.setTrackUntil(device.getTrackUntil());
		return Response.ok(devicePayload).build();
	}

	@RequireUserAccount
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "list")
	public Response getList() {
		UserAccount account = accountProxy.getAccount();

		DeviceStore deviceStore = new DeviceStore(em);
		Collection<Device> deviceList = deviceStore.getAllByUserAccount(account);

		ArrayList<String> hashedMacAddresses = new ArrayList<>();
		for (Device device : deviceList) {
			hashedMacAddresses.add(device.getHardwareIdentifier().toString());
		}

		DeviceIndexPayload deviceIndexPayload = new DeviceIndexPayload();
		deviceIndexPayload.setIndex(hashedMacAddresses);
		return Response.ok(deviceIndexPayload).build();
	}

	@RequireUserAccount
	@DELETE
	@Produces(value = MediaType.APPLICATION_JSON)
	@Path(value = "registry/{id}")
	public Response postUnregister(@PathParam("id") String id) {
		UserAccount account = accountProxy.getAccount();

		DeviceStore deviceStore = new DeviceStore(em);

		// Create a DeviceHashTableManager and register it as a listener, so the DeviceHashTable gets updated if this request causes a database change.
		DeviceHashTableManager manager = new DeviceHashTableManager(deviceStore, deviceHashTableStore);
		deviceStore.registerDeviceStoreListener(manager);

		Device device = deviceStore.get(account, HashedMacAddress.fromString(id));
		if (device != null) {
			deviceStore.remove(device);
		}

		return Response.ok().build();
	}
}
