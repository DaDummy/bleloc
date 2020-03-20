package edu.kit.informatik.pse.bleloc.client.controller;

import android.view.View;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.DashboardActivity;
import edu.kit.informatik.pse.bleloc.client.model.user.AuthenticationResult;
import org.junit.*;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class DashboardActivityTest {

		@Rule
		public ActivityTestRule<DashboardActivity>
			mainActivityActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);
		private DashboardActivity mainActivity = null;

		@Before
		public void setUp(){
			mainActivity = mainActivityActivityTestRule.getActivity();
		}

		@Test
		public void testLaunch(){
			View view = mainActivity.findViewById(R.id.fab);
			Assert.assertNotNull(view);
		}

		@Test
		public void onReceiveLogoutResultTest(){
			mainActivity.onReceiveLogoutResult(AuthenticationResult.SUCCESS);
			Assert.assertTrue(mainActivity.isFinishing());
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
