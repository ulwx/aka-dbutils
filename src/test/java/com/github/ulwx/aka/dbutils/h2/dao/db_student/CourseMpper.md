getRSet
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
order by "id" asc

getRSetPage
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%}
@}
@if( $$:classHours ){ 
and "class_hours" in(#{classHours})
@} 
order by "id"

getOneCourse
===
SELECT "name", "class_hours" 
/* 和 class_hours as classHours 等效，classHours对应到JavaBean的属性名 */ 
FROM "course" WHERE 1 = 1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
@if( $$:teacherId ){ 
and "teacher_id"=#{teacherId}
@}

getCoursesByIds
===
select "id","name" ,"class_hours" as classHours,"creatime" from "course" where 1=1 
@if( $$:name ){  
and "name" like #{name%} 
@} 
@if($$:ids){ 
and "id" in (#{ids})
@} 
order by "id" asc

getCouseList
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@}

getCouseListPage
====
select * from "course" where 1=1 
@ if( $$:name ){ 
and "name" like #{name%} 
@}
order by "id"

getCouseListPageCount
====
select count(1) from "course" where 1=1 
@if( $$:name ){
and "name" like #{name%}
@}


addCourse
====
INSERT INTO "course" (
"name",
"class_hours",
"creatime"
)
VALUES
(
#{name},
#{classHours},
#{creatime}
)


addCourseReturnKey
====
INSERT INTO "course" (
"name",
"class_hours",
"creatime"
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
"course"
SET
"name" = #{name},
"class_hours" = #{classHours},
"creatime" = #{creatime} WHERE "id" = #{id}

dropCourse
====
drop table "course"

getOneString
===
select 'asdfg'

getOneInteger
===
select 123

getOneBigInteger
===
select  123

getOneBigIntegerList
===
select  (123) union select (234) union select (456)

getOneLocalDateTime
===
select  parsedatetime('2014-04-22 15:47:06','yyyy-MM-dd HH:mm:ss')

getOneTimestamp
===
select  parsedatetime('2014-04-22 15:47:06','yyyy-MM-dd HH:mm:ss')

getOneTimestampList
===
select  parsedatetime('2014-04-22 15:47:06','yyyy-MM-dd HH:mm:ss') union select parsedatetime('2015-04-22 15:47:06','yyyy-MM-dd HH:mm:ss')

