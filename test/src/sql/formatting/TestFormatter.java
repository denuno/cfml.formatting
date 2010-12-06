package sql.formatting;

import org.junit.Before;
import org.junit.Test;

public class TestFormatter {
	
	protected SQLFormatter fFormatter;
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testFormatter() {
		fFormatter = new SQLFormatter(
				"select i.obj#,i.ts#,i.file#,i.block#,i.intcols,i.type#,i.flags,i.property,i.pctfree$,i.initrans,i.maxtrans,i.blevel,i.leafcnt,i.distkey,i.lblkkey,i.dblkkey,i.clufac,i.cols,i.analyzetime,i.samplesize,i.dataobj#,nvl(i.degree,1),nvl(i.instances,1),i.rowcnt,mod(i.pctthres$,256),i.indmethod#,i.trunccnt,n vl(c.unicols,0),nvl(c.deferrable#+c.valid#,0),nvl( i.spare1,i.intcols),i.spare4,i.spare2,i.spare6,dec ode(i.pctthres$,null,null,mod(trunc(i.pctthres$/25 6),256)),ist.cachedblk,ist.cachehit,ist.logicalrea d from ind$ i, ind_stats$ ist, (select enabled, min(cols) unicols,min(to_number(bitand(defer,1))) deferrable#,min(to_number(bitand(defer,4))) valid# from cdef$ where obj#=:1 and enabled > 1 group by enabled) c where i.obj#=c.enabled(+) and i.obj# = i st.obj#(+) and i.bo#=:1 order by i.obj#");
		String formatted = fFormatter.format();
		System.out.println(formatted);
	}
	
}
