package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.backoffice.controller.BackofficeSettingsInitializer;
import edu.kit.informatik.pse.bleloc.backoffice.controller.Helper;
import edu.kit.informatik.pse.bleloc.model.settings.Setting;
import edu.kit.informatik.pse.bleloc.model.settings.SettingsRegexUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

public class SettingsTest extends BackofficeTest {
	private Settings settings;

	public SettingsTest() {
		BackofficeSettingsInitializer settingsInitializer = new BackofficeSettingsInitializer();
		Helper.initializeBackofficeSettingsInitializer(settingsInitializer, em);
		settingsInitializer.contextInitialized(null);

		settings = new Settings();
		injectObjectsInto(settings);
	}

	@Test
	public void showPage() {
		Assert.assertNotNull(settings.showPage());
	}

	@Test
	public void doSet00() {
		List<StringPair> validEntries = Arrays.asList(
			new StringPair("general.trackingDuration", "4"),
			new StringPair("plugins.publicBeacons", "on")
		);
		List<StringPair> invalidEntries = Arrays.asList(
			new StringPair("plugins.gamification", "foo"),
			new StringPair("general.maxDeviceNumber", "0"),
			new StringPair("general.maxDeviceNumber", "1")
		);
		this.generalTestDoSet(validEntries, invalidEntries);
	}

	@Test
	public void doSet01() {
		List<StringPair> validEntries = Arrays.asList(
			new StringPair("plugins.statistics", "on")
		);
		List<StringPair> invalidEntries = Arrays.asList(
			new StringPair("nonExistent.setting", "4"),
			new StringPair("incorrect id format", "foo"),
			new StringPair("general.maxDeviceNumber", "foo")
		);
		this.generalTestDoSet(validEntries, invalidEntries);
	}

	private void generalTestDoSet(List<StringPair> validEntries, List<StringPair> invalidEntries) {
		// note for adjusting tests: multi-mappings may cause issues, check doSet JavaDoc

		MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
		for (StringPair sp : validEntries) {
			map.add(sp.name, sp.value);
		}
		for (StringPair sp : invalidEntries) {
			map.add(sp.name, sp.value);
		}
		Assert.assertNotNull(settings.doSet(map));
		// convert HTML-form boolean input "on" to Java boolean output "true"
		for (StringPair sp : validEntries) {
			if (sp.value.equals("on")) {
				sp.value = "true";
			}
		}

		Map<String, Object> result = settings.showPage();
		Assert.assertNotNull(result);
		List<Settings.SettingsGroup> groups = (List<Settings.SettingsGroup>) result.get("settings");
		Assert.assertNotNull(groups);

		List<StringPair> resultData = new ArrayList<>();
		for (Settings.SettingsGroup g : groups) {
			for (Settings.Setting s : g.getSettings()) {
				resultData.add(new StringPair(s.getTextId(), s.getValue()));
			}
		}

		System.out.println("Result:  " + resultData);
		System.out.println("Valid:   " + validEntries);
		System.out.println("Invalid: " + invalidEntries);
		Assert.assertTrue(resultData.containsAll(validEntries));
		for (StringPair sp : invalidEntries) {
			Assert.assertFalse(resultData.contains(sp));
		}
	}

	@Test
	public void testSettingGetter() {
		Map<String, Object> data = settings.showPage();
		Settings.Setting  setting = ((List<Settings.SettingsGroup>) data.get("settings")).get(0).getSettings().get(0);
		assert setting.getTextId().matches(SettingsRegexUtil.TEXT_ID + "\\." + SettingsRegexUtil.TEXT_ID);
		assert setting.getGroupTextId().matches(SettingsRegexUtil.TEXT_ID);
		assert setting.getLabel().matches(SettingsRegexUtil.TYPE_TEXT);
		assert setting.getDescription().matches(SettingsRegexUtil.TYPE_TEXT);
		Setting.Type type;
		Assert.assertNotNull(type = setting.getType());
		assert setting.getValue().matches(type.getValueFormatRegex());
	}

	private class StringPair {
		String name;
		String value;

		StringPair (String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (o instanceof StringPair) {
				StringPair sp = (StringPair) o;
				return this.name.equals(sp.name) && this.value.equals(sp.value);
			}
			return false;
		}

		@Override
		public String toString() {
			return "(" + this.name + "|" + this.value + ")";
		}
	}
}
