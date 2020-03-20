package edu.kit.informatik.pse.bleloc.client.model.settings;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.controller.activities.ResultActivity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

@RunWith(AndroidJUnit4.class)
public class SettingsTest {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	Settings settings = new Settings(preferences);

	@Test
	public void initialTest(){
		Assert.assertNotNull(settings);
	}

	@Test
	public void setMapProviderTest() {
		settings.setMapProvider("MAP_PROVIDER");
		Assert.assertEquals(settings.getMapProvider(), "MAP_PROVIDER");
	}

	@Test
	public void setBackgroundScanningEnabledTest() {
		settings.setBackgroundScanningEnabled(true);
		Assert.assertTrue(settings.isBackgroundScanningEnabled());
	}

	@Test
	public void setMapProviderNoTest() {
		settings.setMapProviderNo(1);
		Assert.assertEquals(settings.getMapProviderNo(),1);
	}

	@Test
	public void getMapProviderActivityTest(){
		settings.setMapProviderNo(1);
		Assert.assertEquals(settings.getMapProviderActivity(), ResultActivity.class);
	}
}
