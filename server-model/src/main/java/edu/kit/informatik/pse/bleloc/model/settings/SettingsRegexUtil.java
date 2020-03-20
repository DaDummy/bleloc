package edu.kit.informatik.pse.bleloc.model.settings;

/**
 * Contains regular expressions for {@link Setting} and related classes.
 */
public final class SettingsRegexUtil {
	private SettingsRegexUtil () {
		// static utility class
	}

	/**
	 * Type expression to verify boolean format ('true' or 'false').
	 */
	public static final String TYPE_BOOLEAN = "(true|false)";

	/**
	 * Type expression to verify text (e.g. for descriptions).
	 */
	public static final String TYPE_TEXT = ".*";

	/**
	 * Type expression to verify numbers (non-empty String of numerals with optional preceding '-').
	 */
	public static final String TYPE_NUMBER = "-?[0-9]+";

	/**
	 * Verifier for textual IDs (camel-case String starting lowercase, containing letters only).
	 */
	public static final String TEXT_ID = "[a-z][a-zA-Z]*";
}
