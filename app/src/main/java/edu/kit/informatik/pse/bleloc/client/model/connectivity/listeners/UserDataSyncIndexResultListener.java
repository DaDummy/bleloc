package edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners;

import edu.kit.informatik.pse.bleloc.payload.UserDataIndexPayload;

import java.util.EventListener;

public interface UserDataSyncIndexResultListener extends EventListener {

	public void onReceiveUserDataSyncIndex(UserDataIndexPayload payload);
}
