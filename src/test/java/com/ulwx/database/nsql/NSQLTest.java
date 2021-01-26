package com.github.ulwx.database.nsql;

import com.github.ulwx.tool.support.ObjectUtils;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NSQLTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(NSQLTest.class);
	
	public void testParseSqlWithStoredProc(){
		String sql = "call #{ret}=#{ parm1 } and b=#{ parm2} #33 #{parm3 }";
		// System.out.println(ObjectUtils.toString(NSQL.parse(sql)));
		Map<String, Object> mp=new HashMap<String,Object>();
		mp.put("ret:out", String.class);
		mp.put("parm1:in", "3345");
		mp.put("parm2:out", Integer.class);
		mp.put("parm3:inout", 12345);
		NSQL nsql=new NSQL();
		nsql=NSQL.parseSql(sql, mp, true,nsql);
		
		log.info("nsql="+ObjectUtils.toString(nsql));
	}
	public void testParseSqlWithSql(){
		String sql = "select * from tt where s=#{ %sysRightCode% } and b=#{ sysRightName} #33 #{aaa }";
		// System.out.println(ObjectUtils.toString(NSQL.parse(sql)));
		Map<String, Object> mp=new HashMap<String,Object>();
		mp.put("sysRightCode", "3345");
		mp.put("sysRightName", "777");
		mp.put("aaa", 12345);
		NSQL nsql=new NSQL();
		nsql=NSQL.parseSql(sql, mp, false,nsql);
		
		log.info("nsql="+ObjectUtils.toString(nsql));
		
	}
	

}
