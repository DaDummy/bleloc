package edu.kit.informatik.pse.bleloc.client.android;

import android.view.View;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.activities.LoginActivity;
import org.hamcrest.Matcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalTestCases {
	static final String Username = "user1";
	static final String Password = "userPWD";
	static final String WrongPassword = "somePWD";
	static final String DeviceAlias = "MyBike";

	@Rule
	public IntentsTestRule<LoginActivity> activityRule = new IntentsTestRule<>(LoginActivity.class);

	@Test
	public void GTC10_1() {
		onView(withId(R.id.signup)).perform(click());

		checkIsOnActivity(R.string.title_activity_sign_up);

		onView(withId(R.id.username)).perform(typeText(Username));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.password)).perform(typeText(Password));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.passwordconfirm)).perform(typeText(Password));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.accept)).perform(click());
		onView(withId(R.id.signup)).perform(click());

		checkIsOnActivity(R.string.title_activity_dashboard);
	}

	@Test
	public void GTC10_2() {
		onView(withId(R.id.signup)).perform(click());

		checkIsOnActivity(R.string.title_activity_sign_up);

		onView(withId(R.id.username)).perform(typeText(Username));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.password)).perform(typeText(Password));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.passwordconfirm)).perform(typeText(Password));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.accept)).perform(click());
		onView(withId(R.id.signup)).perform(click());

		checkIsOnActivity(R.string.title_activity_sign_up);
	}

	@Test
	public void GTC11_1() {
		onView(withId(R.id.username)).perform(typeText(Username));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.password)).perform(typeText(Password));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.login)).perform(click());

		checkIsOnActivity(R.string.title_activity_dashboard);
	}

	@Test
	public void GTC11_2() {
		onView(withId(R.id.username)).perform(typeText(Username));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.password)).perform(typeText(WrongPassword));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.login)).perform(click());

		checkIsOnActivity(R.string.title_activity_login);
	}

	@Test
	public void GTC12_1() {
		// Perform login
		GTC11_1();

		onView(withId(R.id.fab)).perform(click());

		checkIsOnActivity(R.string.title_activity_add_device);

		onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0).perform(click());

		checkIsOnActivity(R.string.title_activity_device_details);

		onView(withId(R.id.itemAlias)).perform(click());

		onView(withText("Change Alias")).check(matches(isDisplayed()));

		onView(withClassName(endsWith("EditText"))).inRoot(isDialog()).perform(clearText(), typeText(DeviceAlias));
		Espresso.closeSoftKeyboard();
		onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

		onView(withText("Change Alias")).check(doesNotExist());

		onView(withId(R.id.textAlias)).check(matches(withText(DeviceAlias)));

		Espresso.pressBack();
		Espresso.pressBack();

		// Wait for animation
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		checkIsOnActivity(R.string.title_activity_dashboard);

		// Wait for sync to start
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
		}

		checkIsOnActivity(R.string.title_activity_dashboard);
	}

	@Test
	public void GTC13() {
		// This is essentially the same test as GTC11-1, just with a different focus on what is actually being tested
		GTC11_1();

		checkIsOnActivity(R.string.title_activity_dashboard);
		onView(withId(R.id.listView)).check(matches(isDisplayed()));
	}

	@Test
	public void GTC14() {
		// Perform login and ensure the device we're looking for is displayed
		GTC13();

		onView(withId(R.id.listView))
			.perform(actionOnItem(hasDescendant(withText(DeviceAlias)), clickChildViewWithId(R.id.details)));

		checkIsOnActivity(R.string.title_activity_device_details);
		onView(withId(R.id.textAlias)).check(matches(withText(DeviceAlias)));
	}

	@Test
	public void GTC15_1() {
		// Perform login and open the device details activity for our device
		GTC14();

		onView(withId(R.id.toggle)).check(matches(isNotChecked()));
		onView(withId(R.id.toggle)).perform(click());
		onView(withId(R.id.toggle)).check(matches(isChecked()));
	}

	@Test
	public void GTC15_2() {
		// Perform login and open the device details activity for our device
		GTC14();

		onView(withId(R.id.toggle)).check(matches(isChecked()));
		onView(withId(R.id.toggle)).perform(click());
		onView(withId(R.id.toggle)).check(matches(isNotChecked()));
	}

	@Test
	public void GTC16() {
		// Perform login and open the device details activity for our device
		GTC14();

		onView(withId(R.id.results)).perform(click());

		onView(withId(R.id.mapview)).check(matches(isDisplayed()));
	}

	@Test
	public void GTC18() {
		// Perform login and open device details of our device
		GTC14();

		onView(withId(R.id.delete)).perform(click());

		checkIsOnActivity(R.string.title_activity_dashboard);
		onView(allOf(withParent(withId(R.id.listView)), withText(DeviceAlias))).check(doesNotExist());
	}

	@Test
	public void GTC19() {
		// Perform login and open device details of our device
		goToSettingsScreen();

		onView(withText("Log out")).perform(click());

		// Wait for animation
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		checkIsOnActivity(R.string.title_activity_login);
	}

	@Test
	public void GTC20() {
		// Perform login and open device details of our device
		goToSettingsScreen();

		onView(withText("Delete account")).perform(click());

		// Wait for animation
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		checkIsOnActivity(R.string.title_activity_login);
	}

	private void goToSettingsScreen() {
		// Perform login
		GTC11_1();

		onView(withId(R.id.item_settings)).perform(click());

		checkIsOnActivity(R.string.title_activity_settings);
	}

	private void checkIsOnActivity(int activityCaption) {
		onView(withText(activityCaption)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
	}

	public static ViewAction clickChildViewWithId(final int id) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return null;
			}

			@Override
			public String getDescription() {
				return "Click on a child view with specified id.";
			}

			@Override
			public void perform(UiController uiController, View view) {
				View v = view.findViewById(id);
				v.performClick();
			}
		};
	}
}
