package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import edu.kit.informatik.pse.bleloc.client.controller.Context;

public class MainActivity extends AppCompatActivity {
	/**
	 * Just redirect to Dashboard or Login.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean loggedIn = ((Context)getApplication()).getAuthenticationManager().isLoggedIn();

		startActivity(new Intent(MainActivity.this, loggedIn ? DashboardActivity.class : LoginActivity.class));
		finish();
	}
}
