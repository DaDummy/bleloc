package edu.kit.informatik.pse.bleloc.client.controller;

import android.view.View;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.AddDeviceActivity;
import org.junit.*;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class AddDeviceActivityTest {

		@Rule
		public ActivityTestRule<AddDeviceActivity> mainActivityActivityTestRule = new ActivityTestRule<>(AddDeviceActivity.class);
		private AddDeviceActivity mainActivity = null;

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
	public void onDestroyTest(){
		getInstrumentation().callActivityOnDestroy(mainActivity);
	}

	@After
		public void tearDown(){
			mainActivity = null;
		}
	}
