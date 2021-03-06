package edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners;

import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import java.util.EventListener;

public interface UserDataSyncUpdateResultListener extends EventListener {

	public void onReceiveUserDataSyncUpdateResult(UserDataPayload payload, SynchronizableObject localObject);
}
