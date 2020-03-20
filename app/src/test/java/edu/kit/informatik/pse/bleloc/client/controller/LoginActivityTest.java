package edu.kit.informatik.pse.bleloc.client.controller;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.LoginActivity;
import edu.kit.informatik.pse.bleloc.client.model.user.AuthenticationResult;
import org.junit.*;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
	private Button login, signUp, skip;
	private EditText username, password;

	@Rule
	public ActivityTestRule<LoginActivity> mainActivityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
	private LoginActivity mainActivity = null;

	@Before
	public void setUp(){
		mainActivity = mainActivityActivityTestRule.getActivity();
	}

	@Test
	public void testLaunch(){
		View view = mainActivity.findViewById(R.id.login);
		Assert.assertNotNull(view);
	}

	@Test
	public void loginButtonPressTest(){
		login = mainActivity.findViewById(R.id.login);
		password = mainActivity.findViewById(R.id.password);
		username = mainActivity.findViewById(R.id.username);
		password.setText("password");
		username.setText("user");
		login.callOnClick();

	}

	@Test
	public void signUpPressTest(){
		signUp = mainActivity.findViewById(R.id.signup);
		password = mainActivity.findViewById(R.id.password);
		username = mainActivity.findViewById(R.id.username);
		password.setText("password");
		username.setText("user");
		signUp.performClick();
	}

	@Test
	public void skipTest(){
		skip = mainActivity.findViewById(R.id.skip);
		skip.performClick();
		Assert.assertTrue(mainActivity.isFinishing());
	}

	@Test
	public void onRequestPermissionsResultTest(){
		String[] permissions = {"ACCESS_COARSE_LOCATION", "ACCESS_FINE_LOCATION"};
		int[] array = {2,1};
		mainActivity.onRequestPermissionsResult(1,permissions,array);
	}

	@Test
	public void onDestroyTest(){
		getInstrumentation().callActivityOnDestroy(mainActivity);
	}

	@Test
	public void onReceiveLoginResultTest(){
		mainActivity.onReceiveLoginResult(AuthenticationResult.SUCCESS);
		Assert.assertTrue(mainActivity.isFinishing());
	}

	@After
	public void tearDown(){
		mainActivity = null;
	}
}
