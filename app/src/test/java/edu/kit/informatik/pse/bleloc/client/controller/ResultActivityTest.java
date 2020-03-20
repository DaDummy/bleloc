package edu.kit.informatik.pse.bleloc.client.controller;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.ResultActivity;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;

import static android.app.Activity.RESULT_OK;

@RunWith(AndroidJUnit4.class)
public class ResultActivityTest {

	@Rule
	public ActivityTestRule<ResultActivity> mainActivityActivityTestRule = new ActivityTestRule<>(ResultActivity.class);
	private ResultActivity mainActivity = null;

	@Before
	public void setUp(){
		mainActivity = mainActivityActivityTestRule.getActivity();
	}

	@Test
	public void testLaunch(){
		View view = mainActivity.findViewById(R.id.listView);
		Assert.assertNotNull(view);
	}

	@Test
	public void onCreateOptionMenuTest(){
		Menu menu = new RoboMenu(ApplicationProvider.getApplicationContext());
		Assert.assertTrue(mainActivity.onCreateOptionsMenu(menu));
	}

	@Test
	public void  onActivityResultTest(){
		Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ResultActivity.class);
		mainActivity.onActivityResult(1, RESULT_OK, intent);
	}

	@Test
	public void onOptionsItemSelectedTest(){
		MenuItem menuItem = new RoboMenuItem(R.id.item_export);
		Assert.assertTrue(mainActivity.onOptionsItemSelected(menuItem));
		menuItem = new RoboMenuItem(R.id.item_settings);
		Assert.assertFalse(mainActivity.onOptionsItemSelected(menuItem));
	}

	@After
	public void tearDown(){
		mainActivity = null;
	}
}



