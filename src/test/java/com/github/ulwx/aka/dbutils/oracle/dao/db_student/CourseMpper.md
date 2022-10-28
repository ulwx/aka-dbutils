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
and teacher_id=#{teacherId}
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
"id",
"name",
"class_hours",
"creatime"
)
VALUES
(
#{@AKA_GEN_ID},
#{name},
#{classHours},
#{creatime}
)


addCourseReturnKey
====
INSERT INTO "course" (
"id",
"name",
"class_hours",
"creatime"
)
VALUES
(
#{@AKA_GEN_ID},
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
select #{name} from dual

getOneInteger
===
select count(#{name}) from dual

getOneIntegerReturnNull
===
select null from dual

getOneBigInteger
===
select 123 from dual

getOneBigIntegerList
===
select 1 from dual union select 2 from dual union select 3 from dual

getOneLocalDateTime
===
SELECT to_timestamp('2014-04-22 15:47:06','yyyy-MM-dd hh24:mi:ss') from dual

getOneTimestamp
===
SELECT to_timestamp('2014-04-22 15:47:06','yyyy-MM-dd hh24:mi:ss') from dual

getOneTimestampList
===
SELECT to_timestamp('2014-04-22 15:47:06','yyyy-MM-dd hh24:mi:ss') from dual
union all SELECT to_timestamp('2015-04-22 15:47:06','yyyy-MM-dd hh24:mi:ss') from dual

