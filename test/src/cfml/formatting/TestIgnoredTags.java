package cfml.formatting;

public class TestIgnoredTags extends FormattingTestCase {
	
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
		assertEquals(fFormatter.format(formatted), preformatted);
		
	}
	
}
