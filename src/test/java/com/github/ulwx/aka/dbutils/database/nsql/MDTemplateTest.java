package com.github.ulwx.aka.dbutils.database.nsql;

import com.github.ulwx.aka.dbutils.tool.support.type.TInteger;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MDTemplateTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(MDTemplateTest.class);
  public void testHanderString(){
	  StringBuilder sb=new StringBuilder();
	  String handLine="AND SysRightName = #{sysRightName} and sext=${myName} and id={#}";
	  TInteger tabNum=new TInteger();
	  
	  MDTemplate.handerString(sb, handLine, tabNum,"","");
	 //String retString=retString+" AND SysRightName = #{sysRightName} and sext="+(args.get("myName") )+" and id={#}";
	  System.out.println(sb);
  }
	
}
