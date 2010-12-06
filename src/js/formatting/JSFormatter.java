package js.formatting;

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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class JSFormatter {
	public static final String WHITESPACE = " \n\r\f\t";
	private String jsSource;
	
	public JSFormatter(String jsText) {
		jsSource = jsText;
	}
	
	public String format() {
		/*
		 * 
		 * result = js_beautify(js_source, { indent_size: indent_size, indent_char: indent_char, preserve_newlines:
		 * preserve_newlines, space_after_anon_function: options.jslint_pedantic, keep_array_indentation:
		 * options.keep_array_indentation, braces_on_own_line: options.braces_on_own_line });
		 */
		Context cx = Context.enter();
		try {
			Scriptable scope = cx.initStandardObjects();
			try {
				String jsBeauty = convertStreamToString(new FileInputStream(
						"/Users/valliant/Projects/java/CFML/cfml.formatting/src/js/formatting/beautify.js"));
				Object res = cx.evaluateString(scope, jsBeauty, "js_beautify", 1, null);
				Object fObj = scope.get("js_beautify", scope);
				if (!(fObj instanceof Function)) {
					System.out.println("js_beautify is undefined or not a function.");
				} else {
					Object functionArgs[] = { jsSource };
					Function f = (Function) fObj;
					Object result = f.call(cx, scope, scope, functionArgs);
					return Context.toString(result);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} finally {
			// Exit from the context.
			Context.exit();
		}
		return "";
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