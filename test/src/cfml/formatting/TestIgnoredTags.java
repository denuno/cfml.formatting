package cfml.formatting;

public class TestIgnoredTags extends FormattingTestCase {
	
	public void testIgnoredTagContent() {
		String formatted, preformatted;
		String[] results;
		
		results = formatFile("test/data/formatting/ignored_tag_content");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(preformatted, formatted);
		assertEquals(preformatted, fFormatter.format(formatted));
		
	}
	
	public void testCommentInSet() {
		String formatted, preformatted;
		String[] results;
		
		results = formatFile("test/data/formatting/comment_in_set");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(preformatted, formatted);
		assertEquals(preformatted, fFormatter.format(formatted));
		
	}
	
	public void testWTF() {
		String formatted, preformatted;
		String[] results;
		
		results = formatFile("test/data/formatting/wtf_1");
		preformatted = results[0];
		formatted = results[1];
		assertEquals(preformatted, formatted);
		assertEquals(preformatted, fFormatter.format(formatted));
		
	}
	
}
