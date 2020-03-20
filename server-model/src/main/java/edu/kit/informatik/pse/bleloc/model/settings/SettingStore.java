package edu.kit.informatik.pse.bleloc.model.settings;

import edu.kit.informatik.pse.bleloc.model.GeneralStore;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the DB-link for {@link Setting}s.
 */
public class SettingStore extends GeneralStore {

	/**
	 * Creates a store instance, that links to the given persistence object.
	 * @param em The persistence object backed by the DB.
	 */
	public SettingStore(EntityManager em) {
		super(em);
		setEntityType(Setting.class);
	}

	/**
	 * Adds a setting to the store and therefore to the DB.<br>
	 *     The operation fails for duplicate settings and throws an exception.
	 * @param setting The setting to add
	 */
	public void add(Setting setting) {
		// check for possible duplicate
		if (this.get(setting.getGroupTextId(), setting.getTextId()) != null) {
			throw new EntityExistsException("Duplicate in group " + setting.getGroupTextId()
				+ ": Setting " + setting.getTextId() + " already exists");
		}

		// all fine, add setting
		addEntry(setting, new Method[]{});
	}

	/**
	 * Updates the setting given by its group and textual ID to the new value.<br>
	 *     The operation fails for non-existent settings and non-matching value formats by exception.
	 * @param groupTextId The setting's group
	 * @param settingTextId The setting's textual ID
	 * @param newValue The new value to update to
	 */
	public void update(String groupTextId, String settingTextId, String newValue) {
		Setting setting = this.get(groupTextId, settingTextId);
		if (setting == null) {
			throw new EntityNotFoundException("Did not find setting " + settingTextId + " in group " + groupTextId);
		}
		// set the new value
		setting.setValue(newValue);
		// write back / update the database entry
		updateEntity(setting, new Method[]{});
	}

	/**
	 * Gets a setting by its group and textual ID.
	 * @param groupTextId The setting's group
	 * @param settingTextId The setting's textual ID
	 * @return The setting defined by group and ID or <code>null</code> if not found
	 */
	public Setting get(String groupTextId, String settingTextId) {
		// get matching settingTextId
		List<Setting> list = getListByColumnValue("textId", settingTextId);
		// filter for matching groupTextId
		list = list.stream().filter(s -> s.getGroupTextId().equals(groupTextId)).collect(Collectors.toList());
		if (list.size() != 1) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * Returns a list containing all settings contained in the DB.
	 * @return All settings as list
	 */
	public List<Setting> getAllSettings() {
		return getList();
	}
}
