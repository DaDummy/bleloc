package edu.kit.informatik.pse.bleloc.client.controller;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.SignUpActivity;
import edu.kit.informatik.pse.bleloc.client.model.user.AuthenticationResult;
import org.junit.*;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest{

	@Rule
	public ActivityTestRule<SignUpActivity> mainActivityActivityTestRule = new ActivityTestRule<>(SignUpActivity.class);
	private SignUpActivity mainActivity = null;

	@Before
	public void setUp(){
		mainActivity = mainActivityActivityTestRule.getActivity();
	}

	@Test
	public void testLaunch(){
		View view = mainActivity.findViewById(R.id.linearLayout);
		Assert.assertNotNull(view);
	}

	@Test
	public void SignUpButtonPressTest(){
		Button signUp = mainActivity.findViewById(R.id.signup);
		EditText passwordConfir = mainActivity.findViewById(R.id.passwordconfirm);
		EditText password = mainActivity.findViewById(R.id.password);
		EditText username = mainActivity.findViewById(R.id.username);
		CheckBox accept = mainActivity.findViewById(R.id.accept);
		passwordConfir.setText("password");
		password.setText("password");
		username.setText("user");
		accept.setChecked(true);
		signUp.performClick();
	}

	@Test
	public void BackButtonPressTest(){
		Button back = mainActivity.findViewById(R.id.back);
		back.performClick();
	}

	@Test
	public void onDestroyTest(){
		getInstrumentation().callActivityOnDestroy(mainActivity);
	}

	@Test
	public void onReceiveRegistrationResultTest(){
		mainActivity.onReceiveRegistrationResult(AuthenticationResult.SUCCESS);
		Assert.assertTrue(mainActivity.isFinishing());
		}

	@After
	public void tearDown(){
		mainActivity = null;
	}

}
