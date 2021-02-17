
getDataCount1
====
SELECT COUNT(SysRightCode) AS dataCount FROM SysRight WHERE (1=1)  
@ if ($$:sysRightCode) {  
 AND SysRightCode like #{%sysRightCode%}
@ }

@ if ($$:sysRightName) {
 AND SysRightName like #{%sysRightName%}
@ }

@ System.out.println("ssss");
@ int f=0;

getDataCount2
====
SELECT COUNT(SysRightCode) AS dataCount FROM SysRight WHERE (1=1)  
@ /* 下面的语句可以用  if($$:sysRightCode) 取代 */
@ if ($$.sysRightCode != null && !"".equals($$.sysRightCode)) {  
 AND SysRightCode = #{sysRightCode}
@ }

@ if ($$.sysRightName != null && !"".equals($$.sysRightName)) {
@ /*${XXX} 为直接拼接*/
 AND SysRightName = ${sysRightName}
@ /*#{XXX} 为预处理模式*/
 AND SysRightName2 = #{sysRightName}
@ /*如果roles为数组或List，则可以直接引用*/
 AND Roles in(#{roles})
@ /*#{%MyUser%} 为like方式*/
 AND MyUser like #{%myUser%}
@ /*#{MyUser%} 为like方式*/
 AND MyUser like #{MyUser%}
@ /* ${=java表达式}*/
 AND s=${="xxx".length()}
@ }

@ System.out.println("ssss");
@ int f=0;

${&getDataCount2}
${&com.github.ulwx.database.test.TestDao.md:getDataCount2}