testUpdate
====
update student set 
@if($$:name){ 
  name=#{name},
@} 
age=${age} where id=#{id}

testVarSubstitution
====
update student set 
@if($$:name){ 
 name=${name},
@} 
age=${age} where id=${id} 
or id in(${ids})
or name like ${%lname}


someSqlfragment
===
and id in (#{ids} )


allMdSyntax
====
SELECT COUNT(1) AS `value` FROM student WHERE (1=1)
/* 下面的语句可以用 if($$:sysRightCode) 取代 */ 
@ if ($$.lname != null && !"".equals($$.lname)) { 
/* #{XXX} 为预编译模式 */ 
AND `name` =#{lname} 
@ }else{ 
and `name` is null 
@} /* ${XXX} 为直接替换模式，即非预编译模式 */ 
AND `name` = ${lname} 
/*如果roles为数组或List，预编译模式*/
AND `name` in(#{lnameList})
/*如果roles为数组或List，为直接替换模式，即非预编译模式*/ 
AND `name` in( ${lnameList} )
/* #{%name%} 只适用于like方式，为预编译模式*/ 
AND `name` like #{%lname%} 
/*#{name%} 只适用于like方式，为预编译模式*/ 
AND `name` like #{lname%} 
/*${MyUser%} 含%则只适用于like方式，直接替换模式，非预编译模式*/ 
AND `name` like ${lname%} 
/* ${=java表达式}*/ 
AND `name`=${="xxx".length()} 
@ int f=0; 
/* java代码 */ 
@System.out.println("lname="+$$.lname+",f="+f);
/* 引用同md文件其它md方法 */ 
${&someSqlfragment}
/* 引用其它md文件其它md方法  */ 
${&com.github.ulwx.aka.dbutils.mysql.dao.db_student.AdvanceUseDao.md:someSqlfragment}






