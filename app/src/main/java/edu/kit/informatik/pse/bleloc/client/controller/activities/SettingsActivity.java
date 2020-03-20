package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.Context;
import edu.kit.informatik.pse.bleloc.client.controller.ScanService;
import edu.kit.informatik.pse.bleloc.client.model.settings.Settings;
import edu.kit.informatik.pse.bleloc.client.model.user.AuthenticationResult;

import edu.kit.informatik.pse.bleloc.client.model.user.LogoutEventListener;

public class SettingsActivity extends AppCompatActivity implements LogoutEventListener {
	/**
	 * Activity to view user settings in the app.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_layout);
		getFragmentManager().beginTransaction().add(R.id.frameLayout, new AccountPreferenceFragment()).commit();
		((Context)getApplication()).getAuthenticationManager().registerListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		((Context)getApplication()).getAuthenticationManager().deregisterListener(this);
	}

	public void onReceiveLogoutResult(AuthenticationResult result) {
		finish();
	}

	public static class AccountPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Settings settings = ((Context)getActivity().getApplication()).getSettings();

			addPreferencesFromResource(R.xml.pref_always);

			SwitchPreference backgroundScanning = (SwitchPreference) findPreference("backgroundScanning");
			backgroundScanning.setChecked(settings.isBackgroundScanningEnabled());
			backgroundScanning.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					boolean enabled = (Boolean) newValue;
					settings.setBackgroundScanningEnabled(enabled);
					if (enabled) {
						getActivity().startService(new Intent(getActivity(), ScanService.class));
					} else {
						getActivity().stopService(new Intent(getActivity(), ScanService.class));
					}
					return true;
				}
			});

			Preference mapProvider = findPreference("mapProvider");
			mapProvider.setSummary(settings.viewNames[settings.getMapProviderNo()]);
			mapProvider.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				private int idx = settings.getMapProviderNo();

				@Override
				public boolean onPreferenceClick(Preference preference) {
					new AlertDialog.Builder(getActivity())
						.setTitle("Map Provider")
						.setSingleChoiceItems(settings.viewNames, idx, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								idx = which;
							}
						})
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								settings.setMapProviderNo(idx);
								mapProvider.setSummary(settings.viewNames[idx]);
							}
						})
						.setNegativeButton("Cancel", null)
						.show();
					return true;
				}
			});

			if (!((Context)getActivity().getApplication()).getAuthenticationManager().isLoggedIn()) {
				return;
			}

			addPreferencesFromResource(R.xml.pref_account);

			Preference logOut = findPreference("logOut");
			logOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					((Context)getActivity().getApplication()).getAuthenticationManager().logout();
					return true;
				}
			});

			Preference changePassword = findPreference("changePassword");
			changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					final EditText input = new EditText(getActivity());
					new AlertDialog.Builder(getActivity())
						.setTitle("Change Password")
						.setView(input)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								((Context)getActivity().getApplication()).getAuthenticationManager().changePassword(input.getText().toString());
							}
						})
						.setNegativeButton("Cancel", null)
						.show();
					return true;
				}
			});

			Preference deleteAccount = findPreference("deleteAccount");
			deleteAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					((Context)getActivity().getApplication()).getAuthenticationManager().delete();
					return true;
				}
			});

			addPreferencesFromResource(R.xml.pref_about);
		}
	}
}
