package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.Context;
import edu.kit.informatik.pse.bleloc.client.controller.ScanService;
import edu.kit.informatik.pse.bleloc.client.model.user.AuthenticationResult;
import edu.kit.informatik.pse.bleloc.client.model.user.LoginEventListener;

public class LoginActivity extends SettingsMenuActivityBase implements LoginEventListener {
	private Button login, signUp, skip;
	private EditText username, password;
	private ProgressBar progressBar;

	/**
	 * Activity for user log in the app.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (Build.VERSION.SDK_INT >= 23
		    && (checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED
		       || checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED)) {
			requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 1);
		}

		((Context) getApplication()).getAuthenticationManager().registerListener(this);

		login = findViewById(R.id.login);
		signUp = findViewById(R.id.signup);
		skip = findViewById(R.id.skip);
		username = findViewById(R.id.username);
		password = findViewById(R.id.password);
		progressBar = findViewById(R.id.progressBar);

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
				((Context) getApplication()).getAuthenticationManager()
				                            .login(username.getText().toString(), password.getText().toString());
			}
		});

		signUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
			}
		});

		skip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		((Context) getApplication()).getAuthenticationManager().deregisterListener(this);
	}

	public void onReceiveLoginResult(AuthenticationResult result) {
		progressBar.setVisibility(View.INVISIBLE);

		if (result == AuthenticationResult.SUCCESS) {
			startActivity(new Intent(this, DashboardActivity.class));
			finish();
		} else {
			// TODO(ca): Show some indicator, that the login failed and once AuthenticationManager/LognRequest support providing a reason for failure, maybe differenciate between different reasons.
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == 1) {
			startService(new Intent(this, ScanService.class));
		}
	}
}
