package cfml.formatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import js.formatting.JSFormatter;
import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceFormatter;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;
import net.htmlparser.jericho.TagType;
import sql.formatting.SQLFormatter;
import cfml.formatting.preferences.FormatterPreferences;
import cfml.parsing.CFMLSource;
import cfml.parsing.cfmentat.tag.CFMLTags;
import cfml.parsing.preferences.ParserPreferences;
import css.formatting.CssCompressor;

public class Formatter {
	
	private static final String lineSeparator = System.getProperty("line.separator");
	// private static final String[] fNoCondenseTagList = "cfif,cffunction,cfcomponent,cfargument,cfscript".split(",");
	private static final String[] fNoCondenseTagList = "cfif".split(",");
	private static String fCurrentIndent;
	private static int MAX_LENGTH = 0;
	private static int col;
	FormatterPreferences fPrefs;
	private ParserPreferences pPrefs;
	LineTrimmer lineTrimmer = new LineTrimmer();
	
	public Formatter(FormatterPreferences prefs) {
		fPrefs = prefs;
		pPrefs = new ParserPreferences();
		pPrefs.setDictionaryDir(fPrefs.getDictionaryDir());
		pPrefs.setCFDictionary(fPrefs.getCFDictionary());
	}
	
	public String format(String contents, FormatterPreferences prefs) {
		String indentation = prefs.getCanonicalIndent();
		String newLine = lineSeparator;
		contents = contents.replaceAll("\\r?\\n", newLine);
		CFMLSource source = new CFMLSource(contents, pPrefs);
		// this won't do anything if collapse whitespace is on!
		// source.ignoreWhenParsing(source.getAllElements(HTMLElementName.SCRIPT));
		// source.ignoreWhenParsing(source.getAllElements(CFMLTagTypes.CFML_SAVECONTENT));
		// source.ignoreWhenParsing(source.getAllElements(CFMLTags.CFML_SCRIPT));
		// source.ignoreWhenParsing(source.getAllElements(CFMLTagTypes.CFML_MAIL));
		
		List<Element> elementList = source.getAllElements();
		/*
		 * for (Element element : elementList) {
		 * System.out.println("-------------------------------------------------------------------------------");
		 * System.out.println(element.getDebugInfo()); if (element.getAttributes() != null)
		 * System.out.println("XHTML StartTag:\n" + element.getStartTag().tidy(true));
		 * System.out.println("Source text with content:\n" + element); }
		 * System.out.println(source.getCacheDebugInfo());
		 */
		boolean enforceMaxLineWidth = prefs.getEnforceMaximumLineWidth();
		boolean tidyTags = prefs.tidyTags();
		boolean collapseWhitespace = prefs.collapseWhiteSpace();
		boolean indentAllElements = prefs.indentAllElements();
		boolean condenseTags = prefs.condenseTags();
		boolean changeTagCase = prefs.changeTagCase();
		boolean changeTagCaseUpper = prefs.changeTagCaseUpper();
		boolean changeTagCaseLower = prefs.changeTagCaseLower();
		int maxLineWidth = prefs.getMaximumLineWidth();
		String[] ignoredTagList = prefs.getIgnoredTags().split(",");
		String[] closeTagList = prefs.getCloseTagsList().split(",");
		String currentIndent = prefs.getInitialIndent();
		
		// displaySegments(source.getAllElements(HTMLElementName.SCRIPT));
		// source.fullSequentialParse();
		
		// java 5 req?
		// System.out.println("Unregistered start tags:");
		// displaySegments(source.getAllTags(StartTagType.UNREGISTERED));
		// System.out.println("Unregistered end tags:");
		// displaySegments(source.getAllTags(EndTagType.UNREGISTERED));
		
		SourceFormatter sourceFormatter = source.getSourceFormatter();
		sourceFormatter.setIndentString(indentation);
		sourceFormatter.setTidyTags(tidyTags);
		sourceFormatter.setIndentAllElements(indentAllElements);
		sourceFormatter.setCollapseWhiteSpace(collapseWhitespace);
		sourceFormatter.setNewLine(newLine);
		String results = sourceFormatter.toString();
		
		CFMLSource formattedSource = new CFMLSource(results);
		StartTagType.setTagTypesIgnoringEnclosedMarkup(new TagType[] { CFMLTags.CFML_COMMENT });
		if (prefs.getCloseTags()) {
			results = closeTagTypes(closeTagList, results);
		}
		if (changeTagCase) {
			if (changeTagCaseLower) {
				results = changeTagCase(results, false);
			} else {
				results = changeTagCase(results, true);
			}
		}
		if (condenseTags) {
			results = condenseTags(results, ignoredTagList, 80);
		}
		results = unformatTagTypes(ignoredTagList, source, formattedSource);
		if (fPrefs.formatSQL()) {
			results = formatQueries(results);
		}
		if (fPrefs.formatJavaScript()) {
			results = formatJavaScript(results);
		}
		if (fPrefs.formatCFScript()) {
			results = formatCFScript(results);
		}
		if (fPrefs.formatCSS()) {
			results = formatCSS(results);
		}
		results = results.replaceAll("(?si)<(cfcomponent[^>]*)>", "<$1>" + newLine);
		results = results.replaceAll("(?si)(\\s+)<(/cfcomponent[^>]*)>", newLine + "$1<$2>");
		results = results.replaceAll("(?si)(\\s+)<(cffunction[^>]*)>", newLine + "$1<$2>");
		results = results.replaceAll("(?si)(\\s+)<(/cffunction[^>]*)>", "$1<$2>" + newLine);
		results = results.replaceAll("(?i)" + newLine + "{3}(\\s+)<(cffunction)", newLine + newLine + "$1<$2");
		results = results.replaceAll("(?si)(\\s+)<(/cffunction[^>]*)>" + newLine + "{3}", "$1<$2>" + newLine + newLine);
		results = results.replaceAll("(?i)" + indentation + "<(cfelse)", "<$1");
		// indent to whatever the current level is
		String[] lines = results.split(newLine);
		StringBuffer indented = new StringBuffer();
		for (int x = 0; x < lines.length; x++) {
			indented.append(currentIndent);
			indented.append(lines[x]);
			indented.append(newLine);
		}
		// indented.setLength(indented.lastIndexOf(newLine));
		// return indented.toString();
		if (!enforceMaxLineWidth) {
			return indented.toString();
		} else {
			return lineTrimmer.formatLineLength(indented.toString(), maxLineWidth, fPrefs.getCanonicalIndent());
		}
	}
	
	private String formatCSS(String intext) {
		CFMLSource source = new CFMLSource(intext, pPrefs);
		List<StartTag> queries = source.getTagsByName("style");
		OutputDocument outputDocument = source.getOutputDocument();
		for (Iterator i = queries.iterator(); i.hasNext();) {
			StartTag tagStart = (StartTag) i.next();
			Element query = ((Tag) tagStart).getElement();
			String formatted = new CssCompressor(query.getContent().toString()).compress(-1) + lineSeparator;
			outputDocument.replace(((Tag) tagStart).getElement().getContent(), lineSeparator + formatted);
		}
		String results = outputDocument.toString();
		results = results.replaceAll("(?si)(\\s+)<(style[^>]*)>", lineSeparator + "$1<$2>");
		return results;
	}
	
	private String formatCFScript(String jsText) {
		CFMLSource source = new CFMLSource(jsText, pPrefs);
		List<StartTag> queries = source.getTagsByName("cfscript");
		OutputDocument outputDocument = source.getOutputDocument();
		for (Iterator i = queries.iterator(); i.hasNext();) {
			StartTag tagStart = (StartTag) i.next();
			Element query = ((Tag) tagStart).getElement();
			String formatted = new JSFormatter(query.getContent().toString()).format() + lineSeparator;
			outputDocument.replace(((Tag) tagStart).getElement().getContent(), lineSeparator + formatted);
		}
		String results = outputDocument.toString();
		results = results.replaceAll("(?si)(\\s+)<(script[^>]*)>", lineSeparator + "$1<$2>");
		return results;
	}
	
	private String formatJavaScript(String jsText) {
		CFMLSource source = new CFMLSource(jsText, pPrefs);
		List<StartTag> queries = source.getTagsByName("script");
		OutputDocument outputDocument = source.getOutputDocument();
		for (Iterator i = queries.iterator(); i.hasNext();) {
			StartTag tagStart = (StartTag) i.next();
			Element query = ((Tag) tagStart).getElement();
			String formatted = new JSFormatter(query.getContent().toString()).format() + lineSeparator;
			outputDocument.replace(((Tag) tagStart).getElement().getContent(), lineSeparator + formatted);
		}
		String results = outputDocument.toString();
		results = results.replaceAll("(?si)(\\s+)<(script[^>]*)>", lineSeparator + "$1<$2>");
		return results;
	}
	
	private String formatQueries(String sqlText) {
		CFMLSource source = new CFMLSource(sqlText, pPrefs);
		List<StartTag> queries = source.getTagsByName("cfquery");
		OutputDocument outputDocument = source.getOutputDocument();
		for (Iterator i = queries.iterator(); i.hasNext();) {
			StartTag tagStart = (StartTag) i.next();
			Element query = ((Tag) tagStart).getElement();
			String formatted = new SQLFormatter(query.getContent().toString()).format() + lineSeparator;
			outputDocument.replace(((Tag) tagStart).getElement().getContent(), formatted);
		}
		String results = outputDocument.toString();
		results = results.replaceAll("(?si)(\\s+)<(cfquery[^>]*)>", lineSeparator + "$1<$2>");
		return results;
	}
	
	private String unformatTagTypes(String[] tagStartTypes, CFMLSource source, CFMLSource formattedSource) {
		List oldCfmailStartTags = source.getAllStartTags();
		List newCfmailStartTags = formattedSource.getAllStartTags();
		OutputDocument outputDocument = formattedSource.getOutputDocument();
		int curTag = 0;
		for (Iterator i = newCfmailStartTags.iterator(); i.hasNext();) {
			StartTag tagStart = (StartTag) i.next();
			if (Arrays.asList(tagStartTypes).contains(tagStart.getName())) {
				outputDocument.replace(((Tag) tagStart).getElement(),
						((Tag) oldCfmailStartTags.get(curTag)).getElement());
			}
			curTag++;
		}
		return outputDocument.toString();
	}
	
	private String closeTagType(String tagStartType, String content) {
		Source source = new Source(content);
		List<StartTag> allTags = source.getAllStartTags();
		OutputDocument outputDocument = new OutputDocument(source);
		for (Iterator i = allTags.iterator(); i.hasNext();) {
			StartTag tagStart = (StartTag) i.next();
			if (tagStart.getName().equals(tagStartType)) {
				if (tagStart.charAt(tagStart.length() - 2) != '/') {
					if (tagStart.charAt(tagStart.length() - 3) != ' ') {
						outputDocument.insert(tagStart.getEnd() - 1, " /");
					} else {
						outputDocument.insert(tagStart.getEnd() - 1, "/");
					}
				} else {
					if (tagStart.charAt(tagStart.length() - 3) != ' ') {
						outputDocument.insert(tagStart.getEnd() - 3, " ");
					}
				}
			}
		}
		return outputDocument.toString();
	}
	
	private String closeTagTypes(String[] tagStartTypes, String content) {
		Source source = new Source(content);
		List<StartTag> allTags = source.getAllStartTags();
		OutputDocument outputDocument = new OutputDocument(source);
		for (Iterator i = allTags.iterator(); i.hasNext();) {
			StartTag tagStart = (StartTag) i.next();
			if (Arrays.asList(tagStartTypes).contains(tagStart.getName())) {
				if (tagStart.charAt(tagStart.length() - 2) != '/') {
					if (tagStart.charAt(tagStart.length() - 2) != ' ') {
						outputDocument.insert(tagStart.getEnd() - 1, " /");
					} else {
						outputDocument.insert(tagStart.getEnd() - 1, "/");
					}
				} else {
					if (tagStart.charAt(tagStart.length() - 3) != ' ') {
						outputDocument.insert(tagStart.getEnd() - 2, " ");
					}
				}
			}
		}
		return outputDocument.toString();
	}
	
	private String condenseTags(String content, String[] ignoredTags, int maxLength) {
		Source source = new Source(content);
		List<StartTag> allTags = source.getAllStartTags();
		OutputDocument outputDocument = new OutputDocument(source);
		for (Iterator i = allTags.iterator(); i.hasNext();) {
			int currentLen = 0;
			StartTag tagStart = (StartTag) i.next();
			currentLen = +(tagStart.getEnd() - tagStart.getBegin());
			if (tagStart.getElement().getEndTag() != null
					&& !Arrays.asList(fNoCondenseTagList).contains(tagStart.getName())
					&& !Arrays.asList(ignoredTags).contains(tagStart.getName())) {
				currentLen = +(tagStart.getElement().getEndTag().getBegin() - tagStart.getEnd());
				if (currentLen < maxLength) {
					outputDocument.replace(tagStart.getElement().getContent(), tagStart.getElement().getContent()
							.toString().trim());
				}
			}
		}
		return outputDocument.toString();
	}
	
	public String changeTagCase(String contents, boolean uppercase) {
		Source source = new Source(contents);
		source.fullSequentialParse();
		OutputDocument outputDocument = new OutputDocument(source);
		List<Tag> tags = source.getAllTags();
		int pos = 0;
		for (Tag tag : tags) {
			Element tagElement = tag.getElement();
			if (tagElement == null) {
				System.out.println(tag.getName());
			} else {
				StartTag startTag = tagElement.getStartTag();
				Attributes attributes = startTag.getAttributes();
				if (attributes != null) {
					for (Attribute attribute : startTag.getAttributes()) {
						if (uppercase) {
							outputDocument.replace(attribute.getNameSegment(), attribute.getNameSegment().toString()
									.toUpperCase());
						} else {
							outputDocument.replace(attribute.getNameSegment(), attribute.getNameSegment().toString()
									.toLowerCase());
						}
					}
				}
				if (uppercase) {
					outputDocument.replace(tag.getNameSegment(), tag.getNameSegment().toString().toUpperCase());
				} else {
					outputDocument.replace(tag.getNameSegment(), tag.getNameSegment().toString().toLowerCase());
				}
				pos = tag.getEnd();
			}
		}
		return outputDocument.toString();
	}
	
	public String format(String string) {
		return format(string, fPrefs);
	}
	
	public String format(URL testFileURL) {
		try {
			return format(testFileURL.getContent().toString(), fPrefs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "FILE NOT FOUND";
	}
	
	public String format(InputStream inStream) {
		try {
			return format(convertStreamToString(inStream), fPrefs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "FILE NOT FOUND";
	}
	
	public String convertStreamToString(InputStream is) throws IOException {
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
