package cfml.formatting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import cfml.formatting.preferences.FormatterPreferences;
import junit.framework.TestCase;

public class TestIgnoredTags extends FormattingTestCase {
	
	private Formatter fFormatter;
	
	public void testFormat() {
		String formatted, preformatted;
		String[] results;
		results = formatFile("test/data/formatting/ignored_tag_content");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(formatted, preformatted);
		results = formatFile("test/data/formatting/comment_in_set");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(formatted, preformatted);
	}
	
}
