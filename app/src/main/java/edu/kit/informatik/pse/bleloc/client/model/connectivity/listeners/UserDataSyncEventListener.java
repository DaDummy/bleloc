package edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners;

import java.util.EventListener;

public interface UserDataSyncEventListener extends EventListener {

	public void onPreSync();

	public void onPostSync();
}
