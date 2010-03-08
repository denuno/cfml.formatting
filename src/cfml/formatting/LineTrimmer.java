package cfml.formatting;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.Tag;
import cfml.parsing.cfmentat.tag.CFMLTags;

public class LineTrimmer {
	private static final String lineSeparator = System.getProperty("line.separator");
	private static String fCurrentIndent;
	private static int MAX_LENGTH = 0;
	private static int col;
	
	public LineTrimmer() {
	}
	
	/*
	 * 
	 * HERE LIES LINE TRIMMING STUPHS
	 */
	public String formatLineLength(String contents, int maxLineWidth, String indentation) {
		String newLine = lineSeparator;
		CFMLTags.register();
		String line = "";
		int indentLen = 0;
		char isWS;
		MAX_LENGTH = maxLineWidth;
		String[] lines = contents.split(newLine);
		StringBuffer indented = new StringBuffer();
		for (int x = 0; x < lines.length; x++) {
			line = lines[x];
			indentLen = 0;
			if (line.length() > 0) {
				isWS = line.charAt(indentLen);
				while ((isWS == ' ' || isWS == '\t') && indentLen < line.length()) {
					isWS = line.charAt(indentLen);
					indentLen++;
				}
				if (indentLen > 0)
					indentLen--;
			}
			fCurrentIndent = line.substring(0, indentLen);
			if (line.length() <= MAX_LENGTH) {
				indented.append(line);
				indented.append(newLine);
				col = 0;
				continue;
			}
			Source source = new Source(line);
			int pos = 0;
			for (Tag tag : source.getAllTags()) {
				if (pos != tag.getBegin()) {
					print(line.subSequence(pos, tag.getBegin()), indented); // print
					// the
					// text
					// between
					// this
					// tag
					// and
					// the
					// last
				}
				formatTag(tag, line, indented);
				pos = tag.getEnd();
			}
			if (pos != line.length()) {
				print(line.subSequence(pos, line.length()), indented); // print
				// the
				// text
				// between
				// the
				// last
				// tag
				// and
				// the
				// end
				// of
				// line
			}
			indented.append(newLine);
			if (col == 0)
				indented.append(fCurrentIndent);
			col = 0;
			
		}
		// indented.setLength(indented.lastIndexOf(newLine));
		return indented.toString();
	}
	
	private static void formatTag(Tag tag, String line, StringBuffer indented) {
		if (tag.length() <= MAX_LENGTH || tag instanceof EndTag) {
			print(fCurrentIndent + tag, indented);
			return;
		}
		StartTag startTag = (StartTag) tag;
		Attributes attributes = startTag.getAttributes();
		if (attributes != null) {
			print(line.substring(startTag.getBegin(), attributes.getBegin()), indented);
			for (Attribute attribute : attributes) {
				print(" ", indented);
				print(attribute, indented);
			}
			print(line.substring(attributes.getEnd(), startTag.getEnd()), indented);
		} else {
			print(startTag, indented);
		}
	}
	
	private static void print(CharSequence text, StringBuffer indented) {
		print(text, true, indented);
	}
	
	private static void print(CharSequence text, boolean splitLongText, StringBuffer indented) {
		if (splitLongText && text.length() > MAX_LENGTH) {
			String[] words = text.toString().split("\\s");
			// indented.append(text.toString().indexOf(words[0]));
			for (int i = 0; i < words.length; i++) {
				print(words[i], false, indented);
				if (i < words.length - 1)
					print(" ", indented);
			}
			return;
		}
		if (col > 0 && col + text.length() > MAX_LENGTH) {
			indented.append(lineSeparator);
			indented.append(fCurrentIndent);
			col = 0;
		}
		indented.append(text);
		col += text.length();
	}
	
}