package cfml.formatting.preferences;

import java.util.Properties;

public class FormatterPreferences {
	
	private class defaults {
		public static final String FORMATTER_WRAP_LONG = "false";
		public static final String FORMATTER_CLOSE_TAGS = "true";
		public static final String FORMATTER_FORMAT_SQL = "false";
		public static final String FORMATTER_MAX_LINE_LENGTH = "80";
		public static final String FORMATTER_ALIGN = "true";
		public static final String FORMATTER_TAB_WIDTH = "2";
		public static final String FORMATTER_TIDY_TAGS = "false";
		public static final String FORMATTER_INSERT_SPACES_FOR_TABS = "false";
		public static final String FORMATTER_COLLAPSE_WHITESPACE = "true";
		public static final String FORMATTER_INDENT_ALL_ELEMENTS = "true";
		public static final String FORMATTER_CHANGE_TAG_CASE = "false";
		public static final String FORMATTER_CHANGE_TAG_CASE_UPPER = "true";
		public static final String FORMATTER_CHANGE_TAG_CASE_LOWER = "false";
		public static final String FORMATTER_INITIAL_INDENT = "";
		public static final String FORMATTER_CONDENSE_TAGS = "true";
	}
	
	Properties fPrefs;
	
	public FormatterPreferences(Properties props) {
		fPrefs = props;
	}
	
	public FormatterPreferences() {
		fPrefs = new Properties();
	}
	
	public String getCanonicalIndent() {
		String canonicalIndent;
		if (!useSpacesInsteadOfTabs()) {
			canonicalIndent = "\t"; //$NON-NLS-1$
		} else {
			String tab = ""; //$NON-NLS-1$
			for (int i = 0; i < getTabWidth(); i++) {
				tab = tab.concat(" "); //$NON-NLS-1$
			}
			canonicalIndent = tab;
		}
		
		return canonicalIndent;
	}
	
	/**
	 * Sets the preference store for these formatting preferences.
	 * 
	 * @param prefs
	 *            the preference store to use as a reference for the formatting preferences
	 */
	public void setProperties(Properties prefs) {
		fPrefs = prefs;
	}
	
	public boolean getEnforceMaximumLineWidth() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_WRAP_LONG,
				defaults.FORMATTER_WRAP_LONG));
	}
	
	public void setEnforceMaximumLineWidth(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_WRAP_LONG, Boolean.toString(heckYes));
	}
	
	public boolean getCloseTags() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_CLOSE_TAGS,
				defaults.FORMATTER_CLOSE_TAGS));
	}
	
	public void setCloseTags(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_CLOSE_TAGS, Boolean.toString(heckYes));
	}
	
	public boolean formatSQL() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_FORMAT_SQL,
				defaults.FORMATTER_FORMAT_SQL));
	}
	
	public void setFormatSQL(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_FORMAT_SQL, Boolean.toString(heckYes));
	}
	
	public int getMaximumLineWidth() {
		return Integer.parseInt(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_MAX_LINE_LENGTH,
				defaults.FORMATTER_MAX_LINE_LENGTH));
	}
	
	public void setMaximumLineWidth(int lineWidth) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_MAX_LINE_LENGTH, Integer.toString(lineWidth));
	}
	
	public boolean wrapLongTags() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_WRAP_LONG,
				defaults.FORMATTER_WRAP_LONG));
	}
	
	public void wrapLongTags(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_WRAP_LONG, Boolean.toString(heckYes));
	}
	
	public boolean alignElementCloseChar() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_ALIGN,
				defaults.FORMATTER_ALIGN));
	}
	
	public void alignElementCloseChar(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_ALIGN, Boolean.toString(heckYes));
	}
	
	public int getTabWidth() {
		return Integer.parseInt(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_TAB_WIDTH,
				defaults.FORMATTER_TAB_WIDTH));
	}
	
	public void setTabWidth(int lineWidth) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_TAB_WIDTH, Integer.toString(lineWidth));
	}
	
	public void setInitialIndent(String indent) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_INITIAL_INDENT, indent);
	}
	
	public String getInitialIndent() {
		return fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_INITIAL_INDENT,
				defaults.FORMATTER_INITIAL_INDENT);
	}
	
	public boolean useSpacesInsteadOfTabs() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_INSERT_SPACES_FOR_TABS,
				defaults.FORMATTER_INSERT_SPACES_FOR_TABS));
	}
	
	public void useSpacesInsteadOfTabs(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_INSERT_SPACES_FOR_TABS, Boolean.toString(heckYes));
	}
	
	public boolean tidyTags() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_TIDY_TAGS,
				defaults.FORMATTER_TIDY_TAGS));
	}
	
	public void tidyTags(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_TIDY_TAGS, Boolean.toString(heckYes));
	}
	
	public boolean collapseWhiteSpace() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_COLLAPSE_WHITESPACE,
				defaults.FORMATTER_COLLAPSE_WHITESPACE));
	}
	
	public void collapseWhiteSpace(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_COLLAPSE_WHITESPACE, Boolean.toString(heckYes));
	}
	
	public boolean indentAllElements() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_INDENT_ALL_ELEMENTS,
				defaults.FORMATTER_INDENT_ALL_ELEMENTS));
	}
	
	public void indentAllElements(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_INDENT_ALL_ELEMENTS, Boolean.toString(heckYes));
	}
	
	public boolean changeTagCase() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE,
				defaults.FORMATTER_CHANGE_TAG_CASE));
	}
	
	public void changeTagCase(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE, Boolean.toString(heckYes));
	}
	
	public boolean changeTagCaseUpper() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE_UPPER,
				defaults.FORMATTER_CHANGE_TAG_CASE_UPPER));
	}
	
	public void changeTagCaseUpper(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE_UPPER, Boolean.toString(heckYes));
	}
	
	public boolean changeTagCaseLower() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE_LOWER,
				defaults.FORMATTER_CHANGE_TAG_CASE_LOWER));
	}
	
	public void changeTagCaseLower(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE_LOWER, Boolean.toString(heckYes));
	}
	
	public boolean condenseTags() {
		return Boolean.parseBoolean(fPrefs.getProperty(FormatterPreferenceConstants.FORMATTER_CONDENSE_TAGS,
				defaults.FORMATTER_CONDENSE_TAGS));
	}
	
	public void condenseTags(boolean heckYes) {
		fPrefs.setProperty(FormatterPreferenceConstants.FORMATTER_CONDENSE_TAGS, Boolean.toString(heckYes));
	}
	// public static boolean affectsFormatting(PropertyChangeEvent event) {
	// String property = event.getProperty();
	// return property.startsWith(FormatterPreferenceConstants.FORMATTER_ALIGN)
	// ||
	// property.startsWith(FormatterPreferenceConstants.FORMATTER_MAX_LINE_LENGTH)
	// ||
	// property.startsWith(FormatterPreferenceConstants.P_INSERT_SPACES_FOR_TABS)
	// || property.startsWith(FormatterPreferenceConstants.P_TAB_WIDTH)
	// || property.startsWith(FormatterPreferenceConstants.FORMATTER_CLOSE_TAGS)
	// || property.startsWith(FormatterPreferenceConstants.FORMATTER_TIDY_TAGS)
	// ||
	// property.startsWith(FormatterPreferenceConstants.FORMATTER_COLLAPSE_WHITESPACE)
	// ||
	// property.startsWith(FormatterPreferenceConstants.FORMATTER_INDENT_ALL_ELEMENTS)
	// ||
	// property.startsWith(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE)
	// ||
	// property.startsWith(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE_LOWER)
	// ||
	// property.startsWith(FormatterPreferenceConstants.FORMATTER_CHANGE_TAG_CASE_UPPER)
	// || property.startsWith(FormatterPreferenceConstants.FORMATTER_WRAP_LONG);
	// }
}