package cfml.formatting;

import cfml.formatting.preferences.FormatterPreferences;

public class TestJSFormatter extends FormattingTestCase {
	
	protected void setUp() throws Exception {
		super.setUp();
		FormatterPreferences prefs = new FormatterPreferences();
		prefs.formatJavaScript(true);
		fFormatter = new Formatter(prefs);
	}
	
	public void testFormatJS() {
		String formatted, preformatted;
		String[] results;
		
		results = formatFile("test/data/formatting/script_tag_content");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(preformatted, formatted);
		assertEquals(preformatted, fFormatter.format(formatted));
		
	}
	
}
