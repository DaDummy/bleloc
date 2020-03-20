package edu.kit.informatik.pse.bleloc.client.controller;

import android.view.Menu;
import android.view.MenuItem;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ActivityTestRule;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.SettingsMenuActivityBase;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;


@RunWith(RobolectricTestRunner.class)
public class SettingsMenuActivityBaseTest {
	@Rule
	public ActivityTestRule<SettingsMenuActivityBase>mainActivityActivityTestRule =
		new ActivityTestRule<>(SettingsMenuActivityBase.class);

	private SettingsMenuActivityBase mainActivity=null;

    @Before
	public void setUp(){
		mainActivity = mainActivityActivityTestRule.getActivity();
    }

	@Test
		public void onCreateOptionsMenuTest() {
		Menu menu = new RoboMenu(ApplicationProvider.getApplicationContext());
		Assert.assertTrue(mainActivity.onCreateOptionsMenu(menu));
	}

	@Test
	public void onOptionsItemSelectedTest(){
		MenuItem menuItem = new RoboMenuItem(R.id.item_settings);
		Assert.assertTrue(mainActivity.onOptionsItemSelected(menuItem));
        menuItem = new RoboMenuItem(R.id.item_export);
		Assert.assertFalse(mainActivity.onOptionsItemSelected(menuItem));
	}

	@After
		public void tearDown(){
		mainActivity = null;
    }
}
