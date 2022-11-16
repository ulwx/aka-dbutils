testDelAll
====
alter table  `course` delete where 1=1

testQueryForResultSet
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
order by id

testQueryForResultSetPage
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
@if( $$:classHours ){ 
and class_hours in(#{classHours})
@} 
order by id

testQueryList
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
@if( $$:classHours ){ 
and class_hours in(#{classHours})
@} 
order by id

testQueryListMdPage
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
order by id

testQueryListWithRowMapper
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
@if( $$:ids ){ 
and id in(#{ids})
@} 
order by id

testQueryOne
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
@if( $$:classHours ){ 
and class_hours in(#{classHours})
@} 
order by id

testQueryMap
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
@if( $$:classHours ){ 
and class_hours in(#{classHours})
@} 
order by id

testQueryMapPage
====
select * from course where 1=1 
@if( $$:name ){ 
and name like #{name%} 
@} 
@if( $$:ids ){ 
and id in(#{ids})
@} 
order by id

testInsertWithMd
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


testUpdateWithMd
====
alter table  `course`
UPDATE
`class_hours` = #{classHours},
`creatime` = #{creatime} WHERE `name` = #{name}


testInsertWithMdReturnKey
====
INSERT INTO course (
name,
class_hours,
creatime
)
VALUES
(
#{name},
#{classHours},
#{creatime}
)

testDeleteWithMd
====
alter table `course`
DELETE 
WHERE `name` = #{name}


testExeSqlScript
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
); 
select * from course;
select * from course where name=#{name};
alter table `course` UPDATE
`class_hours` = #{classHours},
`creatime` = #{creatime} WHERE `name` = #{name};
alter table `course`
DELETE
WHERE `name` = #{name};



