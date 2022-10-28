
testDelAll
====
delete from "course"

testQueryForResultSet
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
order by "id"

testQueryForResultSetPage
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
@if( $$:classHours ){ 
and "class_hours" in(#{classHours})
@} 
order by "id"

testQueryList
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
@if( $$:classHours ){ 
and "class_hours" in(#{classHours})
@} 
order by "id"

testQueryListMdPage
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
order by "id"

testQueryListWithRowMapper
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
@if( $$:ids ){ 
and "id" in(#{ids})
@} 
order by "id"

testQueryOne
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
@if( $$:classHours ){ 
and "class_hours" in(#{classHours})
@} 
order by "id"

testQueryMap
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
@if( $$:classHours ){ 
and "class_hours" in(#{classHours})
@} 
order by "id"

testQueryMapPage
====
select * from "course" where 1=1 
@if( $$:name ){ 
and "name" like #{name%} 
@} 
@if( $$:ids ){ 
and "id" in(#{ids})
@} 
order by "id"

testInsertWithMd
====
INSERT INTO "course" (
"id",
"name",
"class_hours",
"creatime"
)
VALUES
(
/**@AKA_GEN_ID会被DbContext.setGenerateIDForInsert()生成的ID替换**/
#{@AKA_GEN_ID},
#{name},
#{classHours},
#{creatime}
)

testInsertWithMdReturnKey
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

testUpdateWithMd
====
UPDATE
"course"
SET
"class_hours" = #{classHours},
"creatime" = #{creatime} WHERE "name" = #{name}

testDeleteWithMd
====
DELETE FROM
"course"
WHERE "name" = #{name}


testExeSqlScript
====
INSERT INTO "course" (
"id",
"name",
"class_hours",
"creatime"
)
VALUES
(
("SEQ_COURSE_ID".nextval),
#{name},
#{classHours},
#{creatime}
); 
select * from "course";
select * from "course" where "name"=#{name};
UPDATE
"course"
SET
"class_hours" = #{classHours},
"creatime" = #{creatime} WHERE "name" = #{name};
DELETE FROM
"course"
WHERE "name" = #{name};

testStoredProc
====
{#{rs} =call "query_course_proc"(#{name},#{count})}

testStoredFunc
====
{#{count_ret}= call "query_course_cnt_func"(#{name},#{count})}


