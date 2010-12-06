package sql.formatting;

/*
 * 

 select i.obj#,i.ts#,i.file#,i.block#,i.intcols,i.t
 ype#,i.flags,i.property,i.pctfree$,i.initrans,i.ma
 xtrans,i.blevel,i.leafcnt,i.distkey,i.lblkkey,i.db
 lkkey,i.clufac,i.cols,i.analyzetime,i.samplesize,i
 .dataobj#,nvl(i.degree,1),nvl(i.instances,1),i.row
 cnt,mod(i.pctthres$,256),i.indmethod#,i.trunccnt,n
 vl(c.unicols,0),nvl(c.deferrable#+c.valid#,0),nvl(
 i.spare1,i.intcols),i.spare4,i.spare2,i.spare6,dec
 ode(i.pctthres$,null,null,mod(trunc(i.pctthres$/25
 6),256)),ist.cachedblk,ist.cachehit,ist.logicalrea
 d from ind$ i, ind_stats$ ist, (select enabled, mi
 n(cols) unicols,min(to_number(bitand(defer,1))) de
 ferrable#,min(to_number(bitand(defer,4))) valid# f
 rom cdef$ where obj#=:1 and enabled > 1 group by e
 nabled) c where i.obj#=c.enabled(+) and i.obj# = i
 st.obj#(+) and i.bo#=:1 order by i.obj#

 */

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

public class SQLFormatter {
	
	private static final Set BEGIN_CLAUSES = new HashSet();
	private static final Set END_CLAUSES = new HashSet();
	private static final Set LOGICAL = new HashSet();
	private static final Set QUANTIFIERS = new HashSet();
	private static final Set DML = new HashSet();
	private static final Set MISC = new HashSet();
	private static final Set FUNCTIONS = new HashSet();
	private static final boolean UPPERCASE_KEYWORDS = true;
	public static final String WHITESPACE = " \n\r\f\t";
	
	static {
		
		BEGIN_CLAUSES.add("left");
		BEGIN_CLAUSES.add("right");
		BEGIN_CLAUSES.add("inner");
		BEGIN_CLAUSES.add("outer");
		BEGIN_CLAUSES.add("group");
		BEGIN_CLAUSES.add("order");
		
		END_CLAUSES.add("where");
		END_CLAUSES.add("set");
		END_CLAUSES.add("having");
		END_CLAUSES.add("join");
		END_CLAUSES.add("from");
		END_CLAUSES.add("by");
		END_CLAUSES.add("join");
		END_CLAUSES.add("into");
		END_CLAUSES.add("union");
		
		LOGICAL.add("and");
		LOGICAL.add("or");
		LOGICAL.add("when");
		LOGICAL.add("else");
		LOGICAL.add("end");
		
		QUANTIFIERS.add("in");
		QUANTIFIERS.add("all");
		QUANTIFIERS.add("exists");
		QUANTIFIERS.add("some");
		QUANTIFIERS.add("any");
		
		DML.add("insert");
		DML.add("update");
		DML.add("delete");
		
		MISC.add("select");
		MISC.add("on");
		// MISC.add("values");
		
		FUNCTIONS.add("min");
		FUNCTIONS.add("max");
		FUNCTIONS.add("sum");
		FUNCTIONS.add("nvl");
		FUNCTIONS.add("mod");
		FUNCTIONS.add("trunc");
		FUNCTIONS.add("concat");
		FUNCTIONS.add("substr");
		FUNCTIONS.add("top");
		FUNCTIONS.add("limit");
		
	}
	
	String indentString = "    ";
	String initial = "\n    ";
	
	boolean beginLine = true;
	boolean afterBeginBeforeEnd = false;
	boolean afterByOrSetOrFromOrSelect = false;
	boolean afterValues = false;
	boolean afterOn = false;
	boolean afterBetween = false;
	boolean afterInsert = false;
	int inFunction = 0;
	int parensSinceSelect = 0;
	private LinkedList parenCounts = new LinkedList();
	private LinkedList afterByOrFromOrSelects = new LinkedList();
	
	int indent = 1;
	
	StringBuffer result = new StringBuffer();
	StringTokenizer tokens;
	String lastToken;
	String token;
	String lcToken;
	
	public SQLFormatter(String sql) {
		tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[]," + WHITESPACE, true);
	}
	
	public SQLFormatter setInitialString(String initial) {
		this.initial = initial;
		return this;
	}
	
	public SQLFormatter setIndentString(String indent) {
		this.indentString = indent;
		return this;
	}
	
	public String format() {
		
		result.append(initial);
		
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			lcToken = token.toLowerCase();
			
			if ("'".equals(token)) {
				String t;
				do {
					t = tokens.nextToken();
					token += t;
				} while (!"'".equals(t) && tokens.hasMoreTokens()); // cannot handle single quotes
			} else if ("\"".equals(token)) {
				String t;
				do {
					t = tokens.nextToken();
					token += t;
				} while (!"\"".equals(t));
			}
			
			if (afterByOrSetOrFromOrSelect && ",".equals(token)) {
				commaAfterByOrFromOrSelect();
			} else if (afterOn && ",".equals(token)) {
				commaAfterOn();
			}

			else if ("(".equals(token)) {
				openParen();
			} else if (")".equals(token)) {
				closeParen();
			}

			else if (BEGIN_CLAUSES.contains(lcToken)) {
				beginNewClause();
			}

			else if (END_CLAUSES.contains(lcToken)) {
				endNewClause();
			}

			else if ("select".equals(lcToken)) {
				select();
			}

			else if (DML.contains(lcToken)) {
				updateOrInsertOrDelete();
			}

			else if ("values".equals(lcToken)) {
				values();
			}

			else if ("on".equals(lcToken)) {
				on();
			}

			else if (afterBetween && lcToken.equals("and")) {
				misc();
				afterBetween = false;
			}

			else if (LOGICAL.contains(lcToken)) {
				logical();
			}

			else if (isWhitespace(token)) {
				white();
			}

			else {
				misc();
			}
			
			if (!isWhitespace(token))
				lastToken = lcToken;
			
		}
		return result.toString();
	}
	
	private void commaAfterOn() {
		out();
		indent--;
		newline();
		afterOn = false;
		afterByOrSetOrFromOrSelect = true;
	}
	
	private void commaAfterByOrFromOrSelect() {
		out();
		newline();
	}
	
	private void logical() {
		if ("end".equals(lcToken))
			indent--;
		newline();
		out();
		beginLine = false;
	}
	
	private void on() {
		indent++;
		afterOn = true;
		newline();
		out();
		beginLine = false;
	}
	
	private void misc() {
		out();
		if ("between".equals(lcToken)) {
			afterBetween = true;
		}
		if (afterInsert) {
			newline();
			afterInsert = false;
		} else {
			beginLine = false;
			if ("case".equals(lcToken)) {
				indent++;
			}
		}
	}
	
	private void white() {
		if (!beginLine) {
			result.append(" ");
		}
	}
	
	private void updateOrInsertOrDelete() {
		out();
		indent++;
		beginLine = false;
		if ("update".equals(lcToken))
			newline();
		if ("insert".equals(lcToken))
			afterInsert = true;
	}
	
	private void select() {
		out();
		indent++;
		newline();
		parenCounts.addLast(new Integer(parensSinceSelect));
		afterByOrFromOrSelects.addLast(new Boolean(afterByOrSetOrFromOrSelect));
		parensSinceSelect = 0;
		afterByOrSetOrFromOrSelect = true;
	}
	
	private void out() {
		if (BEGIN_CLAUSES.contains(lcToken) || END_CLAUSES.contains(lcToken) || LOGICAL.contains(lcToken)
				|| QUANTIFIERS.contains(lcToken) || DML.contains(lcToken) || MISC.contains(lcToken)
				|| FUNCTIONS.contains(lcToken)) {
			token = token.toUpperCase();
		}
		result.append(token);
	}
	
	private void endNewClause() {
		if (!afterBeginBeforeEnd) {
			indent--;
			if (afterOn) {
				indent--;
				afterOn = false;
			}
			newline();
		}
		out();
		if (!"union".equals(lcToken))
			indent++;
		newline();
		afterBeginBeforeEnd = false;
		afterByOrSetOrFromOrSelect = "by".equals(lcToken) || "set".equals(lcToken) || "from".equals(lcToken);
	}
	
	private void beginNewClause() {
		if (!afterBeginBeforeEnd) {
			if (afterOn) {
				indent--;
				afterOn = false;
			}
			indent--;
			newline();
		}
		out();
		beginLine = false;
		afterBeginBeforeEnd = true;
	}
	
	private void values() {
		indent--;
		newline();
		out();
		indent++;
		newline();
		afterValues = true;
	}
	
	private void closeParen() {
		parensSinceSelect--;
		if (parensSinceSelect < 0) {
			indent--;
			parensSinceSelect = ((Integer) parenCounts.removeLast()).intValue();
			afterByOrSetOrFromOrSelect = ((Boolean) afterByOrFromOrSelects.removeLast()).booleanValue();
		}
		if (inFunction > 0) {
			inFunction--;
			out();
		} else {
			if (!afterByOrSetOrFromOrSelect) {
				indent--;
				newline();
			}
			out();
		}
		beginLine = false;
	}
	
	private void openParen() {
		if (isFunctionName(lastToken) || inFunction > 0) {
			inFunction++;
		}
		beginLine = false;
		if (inFunction > 0) {
			out();
		} else {
			out();
			if (!afterByOrSetOrFromOrSelect) {
				indent++;
				newline();
				beginLine = true;
			}
		}
		parensSinceSelect++;
	}
	
	private static boolean isFunctionName(String tok) {
		final char begin = tok.charAt(0);
		final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
		return isIdentifier && !LOGICAL.contains(tok) && !END_CLAUSES.contains(tok) && !QUANTIFIERS.contains(tok)
				&& !DML.contains(tok) && !MISC.contains(tok);
	}
	
	private static boolean isWhitespace(String token) {
		return WHITESPACE.indexOf(token) >= 0;
	}
	
	private void newline() {
		result.append("\n");
		for (int i = 0; i < indent; i++) {
			result.append(indentString);
		}
		beginLine = true;
	}
	
	public static String join(String seperator, String[] strings) {
		int length = strings.length;
		if (length == 0)
			return "";
		StringBuffer buf = new StringBuffer(length * strings[0].length()).append(strings[0]);
		for (int i = 1; i < length; i++) {
			buf.append(seperator).append(strings[i]);
		}
		return buf.toString();
	}
	
	public static void main(String[] args) {
		if (args.length > 0)
			System.out.println(new SQLFormatter(join(" ", args)).format());
	}
	
}