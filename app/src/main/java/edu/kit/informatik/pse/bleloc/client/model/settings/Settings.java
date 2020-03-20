package edu.kit.informatik.pse.bleloc.client.model.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import edu.kit.informatik.pse.bleloc.client.controller.activities.MapActivity;
import edu.kit.informatik.pse.bleloc.client.controller.activities.ResultActivity;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents settings.
 */
public class Settings {
	public static final List<String> configNames = Arrays.asList("osmdroid", "listView");
	public static final CharSequence[] viewNames = new CharSequence[]{"osmdroid", "List View"};
	public static final Class[] activities = new Class[]{MapActivity.class, ResultActivity.class};

	private SharedPreferences sharedPreferences;

	public Settings(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	public String getMapProvider() {
		return sharedPreferences.getString("mapProvider", null);
	}

	public int getMapProviderNo() {
		return Math.max(0, configNames.indexOf(getMapProvider()));
	}

	public Class getMapProviderActivity() {
		return activities[getMapProviderNo()];
	}

	public void setMapProvider(String value) {
		sharedPreferences.edit().putString("mapProvider", value).apply();
	}

	public void setMapProviderNo(int id) {
		setMapProvider(configNames.get(id));
	}

	public void setBackgroundScanningEnabled(boolean active) {
		sharedPreferences.edit().putBoolean("backgroundScanning", active).apply();
	}

	public boolean isBackgroundScanningEnabled() {
		return sharedPreferences.getBoolean("backgroundScanning", true);
	}
}
