getRSet
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
order by id asc

getRSetPage
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%}
@}
@if( $$:classHours ){ 
and class_hours in(#{classHours})
@} 
order by id

getOneCourse
===
SELECT NAME, class_hours 
/* 和 class_hours as classHours 等效，classHours对应到JavaBean的属性名 */ 
FROM course WHERE 1 = 1 
@if( $$:name ){ 
and name like #{name%} 
@} 
@if( $$:teacherId ){ 
and teacher_id=#{teacherId}
@}

getCoursesByIds
===
select id,name ,class_hours as classHours,creatime from course where 1=1 
@if( $$:name ){  
and name like #{name%} 
@} 
@if($$:ids){ 
and id in (#{ids})
@} 
order by id asc

getCouseList
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@}

getCouseListPage
====
select * from course where 1=1 
@ if( $$:name ){ 
and name like #{name%} 
@}
order by id

getCouseListPageCount
====
select count(1) from course where 1=1 
@if( $$:name ){
and name like #{name%}
@}


addCourse
====
INSERT INTO `course` (
`name`,
`class_hours`,
`creatime`
)
VALUES
(
#{name},
#{classHours},
#{creatime}
)


addCourseReturnKey
====
INSERT INTO `course` (
`name`,
`class_hours`,
`creatime`
)
VALUES
(
#{name},
#{classHours},
#{creatime}
)

updateCourse
====
UPDATE
`course`
SET
`name` = #{name},
`class_hours` = #{classHours},
`creatime` = #{creatime} WHERE `id` = #{id}

dropCourse
====
drop table `course`

getOneString
===
select #{name}

getOneInteger
===
select count(#{name})

getOneIntegerReturnNull
===
select null

getOneBigInteger
===
select 123

getOneBigIntegerList
===
select 1 union select 2 union select 3

getOneLocalDateTime
===
SELECT STR_TO_DATE('2014-04-22 15:47:06','%Y-%m-%d %H:%i:%s')

getOneTimestamp
===
SELECT STR_TO_DATE('2014-04-22 15:47:06','%Y-%m-%d %H:%i:%s')

getOneTimestampList
===
SELECT STR_TO_DATE('2014-04-22 15:47:06','%Y-%m-%d %H:%i:%s')
union all SELECT STR_TO_DATE('2015-04-22 15:47:06','%Y-%m-%d %H:%i:%s')

