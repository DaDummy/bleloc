package edu.kit.informatik.pse.bleloc.model.settings;

import edu.kit.informatik.pse.bleloc.model.AbstractEntry;

import javax.persistence.*;

/**
 * Represents a setting, namely a mapping from a textual ID to some value.
 */
@Entity
@Table(name = "setting")
public class Setting extends AbstractEntry {

	/**
	 * Represents a settings type, like that of a variable.
	 */
	public enum Type {
		NUMBER(SettingsRegexUtil.TYPE_NUMBER),
		TEXT(SettingsRegexUtil.TYPE_TEXT),
		BOOLEAN(SettingsRegexUtil.TYPE_BOOLEAN);

		private String valueFormatRegex;

		Type (String valueFormatRegex) {
			this.valueFormatRegex = valueFormatRegex;
		}

		/**
		 * Tests a value if it satisfies format constraints.<br>
		 *     A simple example is:
		 *     <table border="1">
		 *         <tr>
		 *             <th colspan="2">{@code <Type>}.verifyValueFormat({@code <Value>})</th>
		 *         </tr>
		 *         <tr>
		 *             <th>Type</th>
		 *             <th>Value</th>
		 *             <th>Result</th>
		 *         </tr>
		 *         <tr>
		 *             <td>BOOLEAN</td>
		 *             <td rowspan="3">"true"</td>
		 *             <td><code>true</code></td>
		 *         </tr>
		 *         <tr>
		 *             <td>NUMBER</td>
		 *             <td><code>false</code></td>
		 *         </tr>
		 *         <tr>
		 *             <td>TEXT</td>
		 *             <td><code>true</code></td>
		 *         </tr>
		 *     </table>
		 * @param value The value to be verified
		 * @return <code>true</code> if format rules are met, <code>false</code> otherwise.
		 * @see Type#getValueFormatRegex()
		 * @see String#matches(String)
		 */
		public boolean verifyValueFormat (String value) {
			return value.matches(this.getValueFormatRegex());
		}

		/**
		 * Returns the regular expression used to verify a values format for this type.
		 * @return The regular expression as String.
		 */
		public String getValueFormatRegex () {
			return this.valueFormatRegex;
		}
	}

	/**
	 * The database ID.
	 */
	@Id
	@GeneratedValue(strategy =  GenerationType.SEQUENCE)
	private Long id;

	/**
	 * A settings textual ID is unique in a group. It may occur multiple times throughout different groups, though.
	 */
	@Column(name = "textId", unique = false, nullable = false, length = 256)
	private String textId;

	/**
	 * The group this setting is contained in.
	 */
	@Column(name = "groupTextId", unique = false, nullable = false, length = 256)
	private String groupTextId;

	/**
	 * The label (or title) to be displayed next to the value field.
	 */
	@Column(name = "label", unique = false, nullable = false, length = 256)
	private String label;

	/**
	 * The description to be displayed on mouse-over.
	 */
	@Column(name = "description", unique = false, nullable = false, length = 256)
	private String description;

	/**
	 * The values type.
	 */
	@Column(name = "type", unique = false, nullable = false, length = 256)
	private Type type;

	/**
	 * The settings value.
	 */
	@Column(name = "value", unique = false, nullable = false, length = 256)
	private String value;

	/**
	 * Zero-Argument constructor as required for Entity Beans
	 */
	public Setting () {
	}

	/**
	 * Creates a Setting from given values.<br>
	 *     <code>null</code> inputs are not allowed and result in an exception. The same applies to a wrong value format.<br>
	 *         Constraints are:
	 *         <table border="1">
	 *             <tr>
	 *                 <th>Parameter</th>
	 *                 <th>Constraints</th>
	 *             </tr>
	 *             <tr>
	 *                 <td>textId</td>
	 *                 <td>match {@link SettingsRegexUtil#TEXT_ID}</td>
	 *             </tr>
	 *             <tr>
	 *                 <td>groupTextId</td>
	 *                 <td>match {@link SettingsRegexUtil#TEXT_ID}</td>
	 *             </tr>
	 *             <tr>
	 *                 <td>label</td>
	 *                 <td>any non-empty String</td>
	 *             </tr>
	 *             <tr>
	 *                 <td>description</td>
	 *                 <td>any String</td>
	 *             </tr>
	 *             <tr>
	 *                 <td>type</td>
	 *                 <td></td>
	 *             </tr>
	 *             <tr>
	 *                 <td>value</td>
	 *                 <td>hold up <code>type.verifyValueFormat(value)</code></td>
	 *             </tr>
	 *         </table>
	 * @param textId The textual setting ID, unique in its group
	 * @param groupTextId The group this setting is contained in
	 * @param label The label to display for this setting
	 * @param description The description for what this setting controls
	 * @param type The type of the setting's value
	 * @param value The value of this setting
	 * @return The constructed setting
	 */
	public static Setting createSetting (String textId, String groupTextId, String label, String description, Type type, String value) {
		// check for input validity
		if (textId == null || groupTextId == null || label == null || description == null || type == null || value == null) {
			throw new IllegalArgumentException("Received null value");
		}
		if (!textId.matches(SettingsRegexUtil.TEXT_ID)) {
			throw new IllegalArgumentException("Text ID format does not met: " + textId + " does not match " + SettingsRegexUtil.TEXT_ID);
		}
		if (!groupTextId.matches(SettingsRegexUtil.TEXT_ID)) {
			throw new IllegalArgumentException("Text ID format does not met: " + groupTextId + " does not match " + SettingsRegexUtil.TEXT_ID);
		}
		if (label.equals("")) {
			throw new IllegalArgumentException("Label may not be left empty.");
		}
		if (!type.verifyValueFormat(value)) {
			throw new IllegalArgumentException("Value format does not match its type: " + value + " does not match " + type.valueFormatRegex);
		}

		// create object, fill with data and return

		Setting setting = new Setting();

		setting.textId = textId;
		setting.groupTextId = groupTextId;
		setting.label = label;
		setting.description = description;
		setting.type = type;
		setting.value = value;

		return setting;
	}

	/**
	 * Returns the setting's database ID.
	 * @return The database ID
	 */
	public Long getId () {
		return this.id;
	}

	/**
	 * Returns the textual ID.
	 * @return The textual ID
	 */
	public String getTextId() {
		return this.textId;
	}

	/**
	 * Returns tha (textual) group ID.
	 * @return The group ID
	 */
	public String getGroupTextId() {
		return this.groupTextId;
	}

	/**
	 * Returns the label.
	 * @return The label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Returns the description.
	 * @return The description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns the type.
	 * @return The type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Returns the type.
	 * @return The type
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Assigns a new value to the setting if <code>value</code> matches the {@link Type}s format.<br>
	 *     Illegal formats cause an {@link IllegalArgumentException}.
	 * @param value The new value to be assigned
	 * @see Type#verifyValueFormat(String)
	 */
	public void setValue (String value) {
		if (!this.getType().verifyValueFormat(value)) {
			throw new IllegalArgumentException("Type format mismatch: value " + value + " does not match " + this.getType().getValueFormatRegex());
		}

		this.value = value;
	}
}
