testQueryListOne2One
====
select stu.*,c.* from student stu,student_course sc,course c 
where stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:name ){ 
and stu.name in (#{name})
@} 
order by stu.id

testQueryListOne2OnePage
====
select stu.*,c.* from student stu,student_course sc,course c 
where stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:name ){ 
and stu.name in (#{name})
@} 
order by stu.id

testQueryListOne2OnePageCount
====
select count(1) from student stu,student_course sc,course c 
where stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:name ){
and stu.name in (#{name})
@} 
order by stu.id


testQueryListOne2Many
====
select stu.*, c.*
from student stu,student_many_courses sc,course c 
where stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:name ){ 
and stu.name in (#{name})
@} 
order by stu.id,c.id


testQueryListOne2ManyPage
====
select stu.*, c.*
from student stu,student_many_courses sc,course c where 
stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:names ){ 
and stu.name in (#{names})
@} 
@if( $$:ids ){ 
and stu.id in (#{ids})
@} 
order by stu.id,c.id

getPageIdList
====
select stu.id as [value]
from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:names ){ 
and stu.name in (#{names})
@} 
group by stu.id order by stu.id
