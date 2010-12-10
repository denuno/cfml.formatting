package cfml.formatting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;
import cfml.formatting.preferences.FormatterPreferences;

public class TestExternalDictionary extends TestCase {
	
	private Formatter fFormatter;
	
	protected void setUp() throws Exception {
		FormatterPreferences prefs = new FormatterPreferences();
		prefs.setDictionaryDir("test/data/dictionary");
		prefs.setCFDictionary("awesomedic");
		fFormatter = new Formatter(prefs);
	}
	
	public void testFormat() {
		String formatted, preformatted;
		String[] results;
		results = formatFile("test/data/formatting/ignored_tag_content");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(preformatted, formatted);
		assertEquals(preformatted, fFormatter.format(formatted));
	}
	
	public String[] formatFile(String file) {
		URL testFileURL, formattedFileURL;
		String[] results = new String[2];
		try {
			testFileURL = new URL("file:" + file + ".in.txt");
			formattedFileURL = new URL("file:" + file + ".out.txt");
			String formatted = fFormatter.format(testFileURL.openStream());
			FileWriter fstream = new FileWriter(testFileURL.getPath().replace(".in.txt", ".out.test.txt"));
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(formatted);
			out.close();
			fstream.close();
			results[0] = convertStreamToString(formattedFileURL.openStream());
			results[1] = formatted;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	protected String convertStreamToString(InputStream is) throws IOException {
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
