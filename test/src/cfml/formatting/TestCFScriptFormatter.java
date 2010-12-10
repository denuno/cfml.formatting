package cfml.formatting;

import cfml.formatting.preferences.FormatterPreferences;

public class TestCFScriptFormatter extends FormattingTestCase {
	
	protected void setUp() throws Exception {
		super.setUp();
		FormatterPreferences prefs = new FormatterPreferences();
		prefs.formatCFScript(true);
		fFormatter = new Formatter(prefs);
	}
	
	public void testFormatCFScript() {
		String formatted, preformatted;
		String[] results;
		
		results = formatFile("test/data/formatting/cfscript_content");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(preformatted, formatted);
		assertEquals(preformatted, fFormatter.format(formatted));
		
	}
	
}
