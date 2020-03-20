package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.annotations.RequireBackofficeAccount;
import edu.kit.informatik.pse.bleloc.model.settings.Setting.Type;
import edu.kit.informatik.pse.bleloc.model.settings.SettingStore;

import org.glassfish.jersey.server.mvc.Template;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.RollbackException;
import javax.ws.rs.*;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.*;

@Path("settings")
@Stateless
public class Settings {

	@PersistenceContext
	@SuppressWarnings("unused")
	EntityManager em;

	/**
	 * Shows the settings page.<br/>
	 * The result contains one mapping 'settings' to a grouped list of all settings.
	 *
	 * @return the settings page values
	 */
	@GET
	@Produces("text/html")
	@Template(name = "/settings.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showPage() {
		GroupList groupList = new GroupList();

		SettingStore settingStore = new SettingStore(em);
		List<edu.kit.informatik.pse.bleloc.model.settings.Setting> settingList = settingStore.getAllSettings();
		for (edu.kit.informatik.pse.bleloc.model.settings.Setting s : settingList) {
			groupList.addSettingToGroups(new Setting(s));
		}

		List<SettingsGroup> groups = groupList.getGroups();
		groups.sort(Comparator.naturalOrder());
		for (SettingsGroup g : groups) {
			g.getSettings().sort(Comparator.naturalOrder());
		}

		Map<String, Object> result = new TreeMap<>();
		result.put("settings", groups);
		return result;
	}

	/**
	 * Sets settings.<br/>
	 * The input is checked for setting descriptors only, other values are ignored.<br/>
	 * A setting descriptor consists of the group's text-ID, followed by a dot, followed by the setting's text-ID: group.setting<br/>
	 * For a valid setting, the value format is checked as well. A valid value is applied to the setting, invalid ones are ignored.<br/>
	 * The correct format is a single-mapping value described by their {@link Type}, except for boolean values due to HTML forms:<br/>
	 * As a result of design decisions, turned off checkboxes do not show up in sent HTML form data.
	 * To turn off checkboxes correctly, without the need to scan all settings and disable non-listed, hidden fields supply an (empty) entry.
	 * Active checkboxes' data set contains a value '<code>on</code>', while inactives' does not.
	 * @param formParams form-data from HTML
	 * @return redirect to /settings
	 */
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html")
	@Path("/set")
	@Template(name = "/settings-debug-out.ftl")
	@RequireBackofficeAccount
	public Response doSet(MultivaluedMap<String, String> formParams) {
		SettingStore settingStore = new SettingStore(em);
		Set<Map.Entry<String, List<String>>> entrySet = formParams.entrySet();

		List<String> settingValues;
		String value = "";
		settingsLoop: for (Map.Entry<String, List<String>> entry : entrySet) {
			if (!matchesSettingIdFormat(entry.getKey())) {
				continue;
			}

			settingValues = entry.getValue();

			String[] split = entry.getKey().split("\\.");
			String groupTextId = split[0];
			String settingTextId = split[1];

			edu.kit.informatik.pse.bleloc.model.settings.Setting setting = settingStore.get(groupTextId, settingTextId);
			if (setting == null) {
				continue;
			}
			Type type = setting.getType();
			switch (type) {
				case TEXT:
				case NUMBER:
					if (settingValues.size() != 1) {
						continue settingsLoop;
					}
					value = settingValues.get(0);
					if (!type.verifyValueFormat(value)) {
						continue settingsLoop;
					}
					break;
				case BOOLEAN:
					value = "" + evaluateCheckbox(settingValues);
					break;
			}

			settingStore.update(groupTextId, settingTextId, value);
		}

		return Response.seeOther(UriBuilder.fromResource(Settings.class).build()).build();
	}

	private static boolean matchesSettingIdFormat(String s) {
		String name = "[a-z][a-zA-Z]*";
		String completeRegex = name + "(\\." + name + ")*";
		return s.matches(completeRegex);
	}

	private static boolean evaluateCheckbox(List<String> values) {
		for (String s : values) {
			if (s.equals("on")) {
				return true;
			}
		}
		return false;
	}

	public static class SettingsGroup implements Comparable<SettingsGroup> {
		private String textId;
		private List<Setting> settings;

		public SettingsGroup(String textId) {
			this.textId = textId;
			this.settings = new ArrayList<>();
		}

		public String getTextId () {
			return this.textId;
		}

		public List<Setting> getSettings() {
			return this.settings;
		}

		public void addSetting (Setting setting) {
			this.settings.add(setting);
		}

		@Override
		public int compareTo(SettingsGroup group) {
			return this.getTextId().compareTo(group.getTextId());
		}
	}

	public static class GroupList {
		private List<SettingsGroup> groups;

		public GroupList() {
			this.clearGroups();
		}

		public void clearGroups () {
			this.groups = new ArrayList<>();
		}

		public SettingsGroup getGroupByTextId (String textId) {
			for (SettingsGroup g : this.groups) {
				if (g.getTextId().equals(textId)) {
					return g;
				}
			}
			return null;
		}

		public void addGroup (SettingsGroup group) {
			if (getGroupByTextId(group.textId) == null) {
				this.groups.add(group);
			}
		}

		public void addSettingToGroups (Setting setting) {
			String groupTextId = setting.getGroupTextId();
			SettingsGroup g = getGroupByTextId(groupTextId);
			if (g == null) {
				g = new SettingsGroup(groupTextId);
				this.addGroup(g);
			}

			g.addSetting(setting);
		}

		public List<SettingsGroup> getGroups () {
			return this.groups;
		}
	}

	public class Setting implements Comparable<Setting> {
		private String textId;
		private String groupTextId;
		private String label;
		private String description;
		private String value;
		private Type type;

		public Setting (edu.kit.informatik.pse.bleloc.model.settings.Setting setting) {
			this.textId = setting.getGroupTextId() + "." + setting.getTextId();
			this.groupTextId = setting.getGroupTextId();
			this.label = setting.getLabel();
			this.description = setting.getDescription();
			this.value = setting.getValue();
			this.type = setting.getType();
		}

		public String getTextId() {
			return this.textId;
		}

		public String getGroupTextId() {
			return this.groupTextId;
		}

		public String getLabel() {
			return this.label;
		}

		public String getDescription() {
			return this.description;
		}

		public String getValue() {
			return this.value;
		}

		public Type getType() {
			return this.type;
		}

		@Override
		public int compareTo(Setting setting) {
			return this.getTextId().compareTo(setting.getTextId());
		}
	}
}
