package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.Context;
import edu.kit.informatik.pse.bleloc.client.model.user.AuthenticationResult;

import edu.kit.informatik.pse.bleloc.client.model.user.RegistrationEventListener;

public class SignUpActivity extends AppCompatActivity implements RegistrationEventListener {
	private Button signUp, back;
	private EditText username, password, passwordConfirm;
	private CheckBox accept;
	private ProgressBar progressBar;

	/**
	 * Activity for user registration in the app.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		((Context)getApplication()).getAuthenticationManager().registerListener(this);

		signUp = findViewById(R.id.signup);
		back = findViewById(R.id.back);
		username = findViewById(R.id.username);
		password = findViewById(R.id.password);
		passwordConfirm = findViewById(R.id.passwordconfirm);
		accept = findViewById(R.id.accept);
		progressBar = findViewById(R.id.progressBar);

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		signUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = username.getText().toString();
				String pw = password.getText().toString();

				if (passwordConfirm.getText().toString().equals(pw) && accept.isChecked()) {
					progressBar.setVisibility(View.VISIBLE);
					((Context) getApplication()).getAuthenticationManager().register(name, pw);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		((Context)getApplication()).getAuthenticationManager().deregisterListener(this);
	}

	public void onReceiveRegistrationResult(AuthenticationResult result) {
		finish();
	}
}
