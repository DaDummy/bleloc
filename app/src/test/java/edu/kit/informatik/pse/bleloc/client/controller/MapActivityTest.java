package edu.kit.informatik.pse.bleloc.client.controller;

import android.view.View;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.MapActivity;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

	@Rule
	public ActivityTestRule<MapActivity> mainActivityActivityTestRule = new ActivityTestRule<>( MapActivity.class);
	private  MapActivity mainActivity = null;

	@Before
	public void setUp() {
		mainActivity = mainActivityActivityTestRule.getActivity();
	}

	@Test
	public void testLaunch(){
		View view = mainActivity.findViewById(R.id.mapview);
		Assert.assertNotNull(view);
	}

	@After
	public void tearDown(){
		mainActivity = null;
		}
}
