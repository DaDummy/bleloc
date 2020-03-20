package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import edu.kit.informatik.pse.bleloc.client.BuildConfig;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.BloomFilterDownloadResultListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EventListener;
import java.util.Set;
import java.util.HashSet;

public abstract class BackendRequest<E extends EventListener> {

	private String cookie;

	protected Set<E> listenerSet;

	protected BackendRequest() {
		listenerSet = new HashSet<>();
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public void registerListener(E listener) {
		listenerSet.add(listener);
	}

	public void deregisterListener(E listener) {
		listenerSet.remove(listener);
	}

	protected HttpURLConnection connect(String requestMethod, String path) throws IOException {
		HttpURLConnection connection = (HttpURLConnection)new URL(BuildConfig.BASE_URL + path).openConnection();
		connection.setRequestMethod(requestMethod);
		connection.addRequestProperty("User-Agent", BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME + " " + System.getProperty("http.agent"));
		if (cookie != null) {
			connection.addRequestProperty("Cookie", "userAuth=" + cookie);
		}
		return connection;
	}

	protected abstract void handle() throws IOException;
	protected abstract void result();
}
