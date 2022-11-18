


DROP FUNCTION IF EXISTS "public"."query_course_proc"(IN "v_cname" varchar, in v_cnt int);
-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS "public"."course";
CREATE TABLE "public"."course" (
  "id" int4 NOT NULL ,
  "name" varchar(20) COLLATE "pg_catalog"."default",
  "class_hours" int4,
  "teacher_id" int4,
  "creatime" timestamp(6),
  PRIMARY KEY ("id")
)
;
COMMENT ON COLUMN "public"."course"."id" IS '课程id';
COMMENT ON COLUMN "public"."course"."name" IS '课程名称';
COMMENT ON COLUMN "public"."course"."class_hours" IS '学时';
COMMENT ON COLUMN "public"."course"."teacher_id" IS '对应于db_teacher数据库里的teacher表';
COMMENT ON COLUMN "public"."course"."creatime" IS '建立时间';
COMMENT ON TABLE "public"."course" IS '课程';

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO "public"."course" VALUES (1, 'course1', 11, 1, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (2, 'course2', 12, 2, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (3, 'course3', 13, 3, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (4, 'course4', 10, 4, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (5, 'course5', 11, 1, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (6, 'course6', 12, 1, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (7, 'course7', 13, 2, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (8, 'course8', 14, 2, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (9, 'course9', 15, 3, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (10, 'course10', 16, 4, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (11, 'course11', 17, 1, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (12, 'course12', 18, 0, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (13, 'course13', 19, 0, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (14, 'course14', 20, 2, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (15, 'course15', 21, 1, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (16, 'course16', 22, 2, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (17, 'course17', 23, 4, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (18, 'course18', 24, 1, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (19, 'course19', 25, 0, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (20, 'course20', 26, 0, '2021-03-15 22:31:48');
INSERT INTO "public"."course" VALUES (21, 'course21', 27, 0, '2021-03-15 22:31:48');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS "public"."student";
CREATE TABLE "public"."student" (
  "id" int4 NOT NULL ,
  "name" varchar(20) ,
  "age" int4,
  "birth_day" date,
  PRIMARY KEY ("id")
)
;
COMMENT ON COLUMN "public"."student"."id" IS '学生id';
COMMENT ON COLUMN "public"."student"."name" IS '学生姓名';
COMMENT ON COLUMN "public"."student"."age" IS '年龄';
COMMENT ON COLUMN "public"."student"."birth_day" IS '出生日期';
COMMENT ON TABLE "public"."student" IS '学生';

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO "public"."student" VALUES (1, 'student1', 40, '1980-10-08');
INSERT INTO "public"."student" VALUES (2, 'student2', 39, '1981-11-01');
INSERT INTO "public"."student" VALUES (3, 'student3', 38, '1982-10-08');
INSERT INTO "public"."student" VALUES (4, 'student4', 38, '1982-05-08');
INSERT INTO "public"."student" VALUES (5, 'student5', 38, '1982-06-08');
INSERT INTO "public"."student" VALUES (6, 'student6', 38, '1982-07-08');
INSERT INTO "public"."student" VALUES (7, 'student7', 38, '1982-03-08');
INSERT INTO "public"."student" VALUES (8, 'student8', 38, '1982-04-08');
INSERT INTO "public"."student" VALUES (9, 'student9', 38, '1982-06-08');
INSERT INTO "public"."student" VALUES (10, 'student10', 38, '1982-04-08');
INSERT INTO "public"."student" VALUES (11, 'student11', 38, '1982-06-08');
INSERT INTO "public"."student" VALUES (12, 'student12', 38, '1982-07-08');
INSERT INTO "public"."student" VALUES (13, 'student13', 38, '1982-01-08');

-- ----------------------------
-- Table structure for student_course
-- ----------------------------
DROP TABLE IF EXISTS "public"."student_course";
CREATE TABLE "public"."student_course" (
  "id" int4 NOT NULL ,
  "student_id" int4,
  "course_id" int4,
  PRIMARY KEY ("id")
)
;
COMMENT ON COLUMN "public"."student_course"."student_id" IS '学生id';
COMMENT ON COLUMN "public"."student_course"."course_id" IS '课程id';

-- ----------------------------
-- Records of student_course
-- ----------------------------
INSERT INTO "public"."student_course" VALUES (1, 1, 10);
INSERT INTO "public"."student_course" VALUES (2, 2, 13);
INSERT INTO "public"."student_course" VALUES (3, 3, 14);
INSERT INTO "public"."student_course" VALUES (4, 4, 15);
INSERT INTO "public"."student_course" VALUES (5, 5, 12);
INSERT INTO "public"."student_course" VALUES (6, 6, 16);
INSERT INTO "public"."student_course" VALUES (7, 7, 15);
INSERT INTO "public"."student_course" VALUES (8, 8, 12);
INSERT INTO "public"."student_course" VALUES (9, 9, 14);
INSERT INTO "public"."student_course" VALUES (10, 10, 16);
INSERT INTO "public"."student_course" VALUES (11, 11, 20);

-- ----------------------------
-- Table structure for student_many_courses
-- ----------------------------
DROP TABLE IF EXISTS "public"."student_many_courses";
CREATE TABLE "public"."student_many_courses" (
  "id" int4 NOT NULL ,
  "student_id" int4,
  "course_id" int4,
  PRIMARY KEY ("id")
)
;
COMMENT ON COLUMN "public"."student_many_courses"."student_id" IS '学生id';
COMMENT ON COLUMN "public"."student_many_courses"."course_id" IS '课程id';

-- ----------------------------
-- Records of student_many_courses
-- ----------------------------
INSERT INTO "public"."student_many_courses" VALUES (1, 1, 10);
INSERT INTO "public"."student_many_courses" VALUES (2, 1, 13);
INSERT INTO "public"."student_many_courses" VALUES (3, 1, 13);
INSERT INTO "public"."student_many_courses" VALUES (4, 4, 15);
INSERT INTO "public"."student_many_courses" VALUES (5, 4, 12);
INSERT INTO "public"."student_many_courses" VALUES (6, 6, 16);
INSERT INTO "public"."student_many_courses" VALUES (7, 7, 15);
INSERT INTO "public"."student_many_courses" VALUES (8, 7, 12);
INSERT INTO "public"."student_many_courses" VALUES (9, 9, 14);
INSERT INTO "public"."student_many_courses" VALUES (10, 9, 16);
INSERT INTO "public"."student_many_courses" VALUES (11, 9, 20);
INSERT INTO "public"."student_many_courses" VALUES (12, 10, 11);

-- ----------------------------
-- Table structure for t1
-- ----------------------------
DROP TABLE IF EXISTS "public"."t1";
CREATE TABLE "public"."t1" (
  "id" int4 NOT NULL ,
  "a" int4,
  "key_b" timestamp(6),
  "key_c" varchar(30) ,
  PRIMARY KEY ("id")
)
;

-- ----------------------------
-- Records of t1
-- ----------------------------

-- ----------------------------
-- Table structure for t2
-- ----------------------------
DROP TABLE IF EXISTS "public"."t2";
CREATE TABLE "public"."t2" (
  "id" int4 NOT NULL ,
  "a" int4,
  "key_a" int4,
  "key_b" int4,
  PRIMARY KEY ("id")
)
;

-- ----------------------------
-- Records of t2
-- ----------------------------

-- ----------------------------
-- Function structure for query_course_cnt_func
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."query_course_cnt_func"("v_name" varchar);
CREATE OR REPLACE FUNCTION "public"."query_course_cnt_func"("v_name" varchar)
  RETURNS "pg_catalog"."int4" AS $BODY$
declare v_ret int;  
BEGIN
	SELECT count(1) INTO v_ret FROM course WHERE name = v_name;
	RETURN v_ret;
END 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- ----------------------------
-- Function structure for query_course_proc
-- ----------------------------
CREATE OR REPLACE FUNCTION "public"."query_course_proc"("v_cname" varchar, "v_cnt" int4)
              RETURNS TABLE("id1" int4, "name_" varchar, "class_hours_" int4, "teacher_id_" int4, "creatime_" timestamp) AS $BODY$

BEGIN
    RETURN QUERY  SELECT "id" ,"name" ,"class_hours" ,
                         "teacher_id" ,"creatime"  FROM course WHERE name = v_cname;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;



-- ----------------------------
-- Function structure for testproc
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."testproc"();
CREATE OR REPLACE FUNCTION "public"."testproc"()
  RETURNS "pg_catalog"."int4" AS $BODY$
declare i int;  
BEGIN
	   i:=1;
	   select 1 into i from t1;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- ----------------------------
-- sequence
-- ----------------------------

CREATE SEQUENCE "public"."course_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

SELECT setval('"public"."course_id_seq"', 21, true);
ALTER SEQUENCE "public"."course_id_seq" OWNED BY "public"."course"."id";
ALTER SEQUENCE "public"."course_id_seq" OWNER TO  "gpadmin";
alter table "public"."course" alter column "id" set default nextval('"public"."course_id_seq"');
-- -------------------------
CREATE SEQUENCE "public"."student_course_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

SELECT setval('"public"."student_course_id_seq"', 11, true);
ALTER SEQUENCE "public"."student_course_id_seq" OWNED BY "public"."student_course"."id";
ALTER SEQUENCE "public"."student_course_id_seq" OWNER TO  "gpadmin";
alter table "public"."student_course" alter column "id"  set default nextval('"public"."student_course_id_seq"');

-- -----------
CREATE SEQUENCE "public"."student_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

SELECT setval('"public"."student_id_seq"', 13, true);
ALTER SEQUENCE "public"."student_id_seq"
OWNED BY "public"."student"."id";
ALTER SEQUENCE "public"."student_id_seq" OWNER TO  "gpadmin";
alter table "public"."student" alter column "id"  set default nextval('"public"."student_id_seq"');
-- -----------------

CREATE SEQUENCE "public"."student_many_courses_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

SELECT setval('"public"."student_many_courses_id_seq"', 12, true);
ALTER SEQUENCE "public"."student_many_courses_id_seq"
OWNED BY "public"."student_many_courses"."id";
ALTER SEQUENCE "public"."student_many_courses_id_seq" OWNER TO  "gpadmin";
alter table "public"."student_many_courses" alter column "id" set default nextval('"public"."student_many_courses_id_seq"');

--  -------------------
CREATE SEQUENCE "public"."t1_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

SELECT setval('"public"."t1_id_seq"', 1, false);
ALTER SEQUENCE "public"."t1_id_seq"
OWNED BY "public"."t1"."id";
ALTER SEQUENCE "public"."t1_id_seq" OWNER TO  "gpadmin";
alter table "public"."t1" alter column "id" set default nextval('"public"."t1_id_seq"');
-- ----------------

CREATE SEQUENCE "public"."t2_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

SELECT setval('"public"."t2_id_seq"', 1, false);
ALTER SEQUENCE "public"."t2_id_seq"
OWNED BY "public"."t2"."id";
ALTER SEQUENCE "public"."t2_id_seq" OWNER TO  "gpadmin";
alter table "public"."t2" alter column "id" set default nextval('"public"."t2_id_seq"');