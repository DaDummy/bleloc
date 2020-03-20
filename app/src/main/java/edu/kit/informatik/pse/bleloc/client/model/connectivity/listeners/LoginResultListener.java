package edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners;

import java.util.EventListener;

public interface LoginResultListener extends EventListener {

	public void onReceiveLoginResult(java.lang.String cookie);
}
