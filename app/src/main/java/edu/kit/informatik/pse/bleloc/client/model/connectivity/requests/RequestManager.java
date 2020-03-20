package edu.kit.informatik.pse.bleloc.client.model.connectivity.requests;

import android.os.AsyncTask;
import android.util.Log;
import edu.kit.informatik.pse.bleloc.client.model.user.UserData;

import java.io.IOException;

public class RequestManager {
	private UserData userData;

	public RequestManager(UserData userData) {
		this.userData = userData;
	}

	static class RequestTask extends AsyncTask<BackendRequest, Void, BackendRequest> {
		protected BackendRequest doInBackground(BackendRequest... request) {
			try {
				request[0].handle();
			} catch (IOException e) {
				Log.d("RequestManager", "Exception thrown during HTTP request:", e);
				// TODO: error handling
				//return null;
			}
			return request[0];
		}

		protected void onPostExecute(BackendRequest request) {
			if (request != null) {
				request.result();
			}
		}
	}

	public void send(BackendRequest request) {
		request.setCookie(userData.getCookie());
		new RequestTask().execute(request);
	}
}
