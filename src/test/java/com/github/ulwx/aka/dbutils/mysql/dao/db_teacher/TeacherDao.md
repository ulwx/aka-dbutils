testSelect
====
SELECT
`id`,
`name`
FROM
`teacher`
where name like #{lname%}

testSelectIntrans
====
SELECT
`id`,
`name`
FROM
`teacher`
where name like #{lname%}