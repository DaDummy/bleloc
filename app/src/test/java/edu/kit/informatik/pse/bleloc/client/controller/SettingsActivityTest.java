package edu.kit.informatik.pse.bleloc.client.controller;

import android.view.View;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.SettingsActivity;
import edu.kit.informatik.pse.bleloc.client.model.user.AuthenticationResult;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {
	@Rule
	public ActivityTestRule<SettingsActivity> mainActivityActivityTestRule = new ActivityTestRule<>(SettingsActivity.class);
		private SettingsActivity mainActivity = null;

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		mainActivity = mainActivityActivityTestRule.getActivity();
	}

	@Test
	public void testLaunch(){
		View view = mainActivity.findViewById(R.id.frameLayout);
		Assert.assertNotNull(view);
	}

	@Test
	public void onDestroyTest(){
		getInstrumentation().callActivityOnDestroy(mainActivity);
	}

	@Test
	public void onReceiveLogoutResultTest(){
		mainActivity.onReceiveLogoutResult(AuthenticationResult.SUCCESS);
		Assert.assertTrue(mainActivity.isFinishing());
	}

	@After
	public void tearDown(){
		mainActivity = null;
	}
}
