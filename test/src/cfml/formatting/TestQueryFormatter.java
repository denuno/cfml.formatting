package cfml.formatting;

import cfml.formatting.preferences.FormatterPreferences;

public class TestQueryFormatter extends FormattingTestCase {
	
	protected void setUp() throws Exception {
		super.setUp();
		FormatterPreferences prefs = new FormatterPreferences();
		prefs.setFormatSQL(true);
		fFormatter = new Formatter(prefs);
	}
	
	public void testFormatQueries() {
		String formatted, preformatted;
		String[] results;
		
		results = formatFile("test/data/formatting/cfquery_tag_content");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(preformatted, formatted);
		assertEquals(preformatted, fFormatter.format(formatted));
		
	}
	
}
