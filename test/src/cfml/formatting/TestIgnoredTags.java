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

public class TestIgnoredTags extends TestCase {

	private Formatter fFormatter;

	protected void setUp() throws Exception {
		super.setUp();
		FormatterPreferences prefs = new FormatterPreferences();
		fFormatter = new Formatter(prefs);
	}

	public void testFormat() {
		URL testFileURL, formattedFileURL;
		try {
			testFileURL = new URL("file:test/data/formatting/ignored_tag_content.in.txt");
			formattedFileURL = new URL("file:test/data/formatting/ignored_tag_content.out.txt");
			String formatted = fFormatter.format(testFileURL.openStream());
			FileWriter fstream = new FileWriter(testFileURL.getPath().replace(".in.txt", ".out.test.txt"));
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(formatted);
			out.close();
			fstream.close();
			assertEquals(formatted, convertStreamToString(formattedFileURL.openStream()));

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

}
