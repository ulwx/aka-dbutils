getListOne2One
====
select stu.*,c.* from student stu,student_course sc,course c where 
stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:names ){ 
and stu.name in (#{names})
@} 
order by stu.id

getListOne2OnePage
====
select stu.*,c.* from student stu,student_course sc,course c 
where stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:names ){ 
and stu.name in (#{names})
@} 
order by stu.id

getListOne2OnePageCount
====
select count(1) from student stu,student_course sc,course c 
where stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:names ){ 
and stu.name in (#{names})
@} 
order by stu.id


getListOne2Many
====
select stu.*, c.*
from student stu,student_many_courses sc,course c 
where stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:names ){ 
and stu.name in (#{names})
@} 
order by stu.id,c.id


getListOne2ManyPage
====
select stu.*, c.*
from student stu,student_many_courses sc,course c where stu.id=sc.student_id 
and c.id=sc.course_id 
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
from student stu,student_many_courses sc,course c where
stu.id=sc.student_id and c.id=sc.course_id 
@if( $$:names ){ 
and stu.name in (#{names})
@} 
group by stu.id order by stu.id
