/*
 Navicat Premium Data Transfer

 Source Server         : oracle
 Source Server Type    : Oracle
 Source Server Version : 110200 (Oracle Database 11g Enterprise Edition Release 11.2.0.1.0 - 64bit Production
With the Partitioning, OLAP, Data Mining and Real Application Testing options)
 Source Host           : 192.168.137.200:1521
 Source Schema         : DB_STUDENT

 Target Server Type    : Oracle
 Target Server Version : 110200 (Oracle Database 11g Enterprise Edition Release 11.2.0.1.0 - 64bit Production
With the Partitioning, OLAP, Data Mining and Real Application Testing options)
 File Encoding         : 65001

 Date: 26/10/2022 13:49:40
*/


-- ----------------------------
-- Table structure for course
-- ----------------------------

BEGIN EXECUTE IMMEDIATE 'DROP TABLE "DB_STUDENT"."course"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/

CREATE TABLE "DB_STUDENT"."course" (
  "id" NUMBER(11,0) NOT NULL,
  "name" NVARCHAR2(20),
  "class_hours" NUMBER(11,0),
  "teacher_id" NUMBER(11,0),
  "creatime" DATE
)
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 65536 
  NEXT 1048576 
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;
COMMENT ON COLUMN "DB_STUDENT"."course"."id" IS '课程id';
COMMENT ON COLUMN "DB_STUDENT"."course"."name" IS '课程名称';
COMMENT ON COLUMN "DB_STUDENT"."course"."class_hours" IS '学时';
COMMENT ON COLUMN "DB_STUDENT"."course"."teacher_id" IS '对应于db_teacher数据库里的teacher表';
COMMENT ON COLUMN "DB_STUDENT"."course"."creatime" IS '建立时间';
COMMENT ON TABLE "DB_STUDENT"."course" IS '课程';

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO "DB_STUDENT"."course" VALUES ('1', 'course1', '11', '1', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('2', 'course2', '12', '2', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('3', 'course3', '13', '3', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('4', 'course4', '10', '4', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('5', 'course5', '11', '1', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('6', 'course6', '12', '1', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('7', 'course7', '13', '2', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('8', 'course8', '14', '2', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('9', 'course9', '15', '3', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('10', 'course10', '16', '4', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('11', 'course11', '17', '1', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('12', 'course12', '18', '0', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('13', 'course13', '19', '0', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('14', 'course14', '20', '2', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('15', 'course15', '21', '1', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('16', 'course16', '22', '2', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('17', 'course17', '23', '4', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('18', 'course18', '24', '1', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('19', 'course19', '25', '0', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('20', 'course20', '26', '0', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."course" VALUES ('21', 'course21', '27', '0', TO_DATE('2021-03-15 22:31:48', 'SYYYY-MM-DD HH24:MI:SS'));

-- ----------------------------
-- Table structure for student
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE "DB_STUDENT"."student"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE TABLE "DB_STUDENT"."student" (
  "id" NUMBER(11,0) NOT NULL,
  "name" NVARCHAR2(20),
  "age" NUMBER(11,0),
  "birth_day" DATE
)
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 65536 
  NEXT 1048576 
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;
COMMENT ON COLUMN "DB_STUDENT"."student"."id" IS '学生id';
COMMENT ON COLUMN "DB_STUDENT"."student"."name" IS '学生姓名';
COMMENT ON COLUMN "DB_STUDENT"."student"."age" IS '年龄';
COMMENT ON COLUMN "DB_STUDENT"."student"."birth_day" IS '出生日期';
COMMENT ON TABLE "DB_STUDENT"."student" IS '学生';

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO "DB_STUDENT"."student" VALUES ('1', 'student1', '40', TO_DATE('1980-10-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('2', 'student2', '39', TO_DATE('1981-11-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('3', 'student3', '38', TO_DATE('1982-10-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('4', 'student4', '38', TO_DATE('1982-05-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('5', 'student5', '38', TO_DATE('1982-06-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('6', 'student6', '38', TO_DATE('1982-07-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('7', 'student7', '38', TO_DATE('1982-03-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('8', 'student8', '38', TO_DATE('1982-04-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('9', 'student9', '38', TO_DATE('1982-06-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('10', 'student10', '38', TO_DATE('1982-04-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('11', 'student11', '38', TO_DATE('1982-06-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('12', 'student12', '38', TO_DATE('1982-07-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));
INSERT INTO "DB_STUDENT"."student" VALUES ('13', 'student13', '38', TO_DATE('1982-01-08 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));

-- ----------------------------
-- Table structure for student_course
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE "DB_STUDENT"."student_course"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE TABLE "DB_STUDENT"."student_course" (
  "id" NUMBER(11,0) NOT NULL,
  "student_id" NUMBER(11,0),
  "course_id" NUMBER(11,0)
)
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 65536 
  NEXT 1048576 
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;
COMMENT ON COLUMN "DB_STUDENT"."student_course"."student_id" IS '学生id';
COMMENT ON COLUMN "DB_STUDENT"."student_course"."course_id" IS '课程id';

-- ----------------------------
-- Records of student_course
-- ----------------------------
INSERT INTO "DB_STUDENT"."student_course" VALUES ('1', '1', '10');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('2', '2', '13');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('3', '3', '14');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('4', '4', '15');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('5', '5', '12');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('6', '6', '16');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('7', '7', '15');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('8', '8', '12');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('9', '9', '14');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('10', '10', '16');
INSERT INTO "DB_STUDENT"."student_course" VALUES ('11', '11', '20');

-- ----------------------------
-- Table structure for student_many_courses
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE "DB_STUDENT"."student_many_courses"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE TABLE "DB_STUDENT"."student_many_courses" (
  "id" NUMBER(11,0) NOT NULL,
  "student_id" NUMBER(11,0),
  "course_id" NUMBER(11,0)
)
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 65536 
  NEXT 1048576 
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;
COMMENT ON COLUMN "DB_STUDENT"."student_many_courses"."student_id" IS '学生id';
COMMENT ON COLUMN "DB_STUDENT"."student_many_courses"."course_id" IS '课程id';

-- ----------------------------
-- Records of student_many_courses
-- ----------------------------
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('1', '1', '10');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('2', '1', '13');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('3', '1', '13');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('4', '4', '15');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('5', '4', '12');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('6', '6', '16');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('7', '7', '15');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('8', '7', '12');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('9', '9', '14');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('10', '9', '16');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('11', '9', '20');
INSERT INTO "DB_STUDENT"."student_many_courses" VALUES ('12', '10', '11');

-- ----------------------------
-- Table structure for t1
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE "DB_STUDENT"."t1"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE TABLE "DB_STUDENT"."t1" (
  "id" NUMBER(11,0) NOT NULL,
  "a" NUMBER(11,0),
  "key_b" DATE,
  "key_c" NVARCHAR2(30)
)
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;

-- ----------------------------
-- Records of t1
-- ----------------------------

-- ----------------------------
-- Table structure for t2
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE "DB_STUDENT"."t2"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE TABLE "DB_STUDENT"."t2" (
  "id" NUMBER(11,0) NOT NULL,
  "a" NUMBER(11,0),
  "key_a" NUMBER(11,0),
  "key_b" NUMBER(11,0)
)
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;

-- ----------------------------
-- Records of t2
-- ----------------------------

-- ----------------------------
-- Function structure for query_course_cnt_func
-- ----------------------------
CREATE OR REPLACE
   FUNCTION "DB_STUDENT"."query_course_cnt_func"(v_name IN varchar2, v_cnt in out number)
  return NUMBER
AS
    v_ret NUMBER;
BEGIN
SELECT count(1) INTO v_cnt FROM "course" WHERE "name" = v_name;
v_ret:=v_cnt;
RETURN v_ret;
END;
/

-- ----------------------------
-- Function structure for query_course_proc
-- ----------------------------
CREATE OR REPLACE FUNCTION  "query_course_proc"(v_name in VARCHAR2, v_cnt in NUMBER)
RETURN SYS_REFCURSOR
AS
   type_cur SYS_REFCURSOR;
BEGIN
    OPEN type_cur for SELECT "id" ,"name" ,"class_hours" ,
                             "teacher_id" ,"creatime" FROM "course" where "name"=v_name;
RETURN  type_cur;
END;
/

-- ----------------------------
-- Function structure for testproc
-- ----------------------------
CREATE OR REPLACE FUNCTION "testproc"
 RETURN NUMBER
AS
 cnt NUMBER;
BEGIN
select sum(1) into cnt from "t1";
RETURN cnt;
END;
/
-- ----------------------------
-- Sequence structure for SEQ_COURSE_ID
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE "DB_STUDENT"."SEQ_COURSE_ID"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE SEQUENCE "DB_STUDENT"."SEQ_COURSE_ID" MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 start WITH 22 CACHE 20;

-- ----------------------------
-- Sequence structure for SEQ_STUDENT_COURSE_ID
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE "DB_STUDENT"."SEQ_STUDENT_COURSE_ID"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE SEQUENCE "DB_STUDENT"."SEQ_STUDENT_COURSE_ID" MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 start WITH 12 CACHE 20;

-- ----------------------------
-- Sequence structure for SEQ_STUDENT_ID
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE "DB_STUDENT"."SEQ_STUDENT_ID"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE SEQUENCE "DB_STUDENT"."SEQ_STUDENT_ID" MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 start WITH 14  CACHE 20;

-- ----------------------------
-- Sequence structure for SEQ_STUDENT_MANY_COURSES_ID
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE "DB_STUDENT"."SEQ_STUDENT_MANY_COURSES_ID"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/

CREATE SEQUENCE "DB_STUDENT"."SEQ_STUDENT_MANY_COURSES_ID" MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 start WITH 13 CACHE 20;

-- ----------------------------
-- Sequence structure for SEQ_T1_ID
-- ----------------------------

BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE "DB_STUDENT"."SEQ_T1_ID"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE SEQUENCE "DB_STUDENT"."SEQ_T1_ID" MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 CACHE 20;

-- ----------------------------
-- Sequence structure for SEQ_T2_ID
-- ----------------------------

BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE "DB_STUDENT"."SEQ_T2_ID"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE SEQUENCE "DB_STUDENT"."SEQ_T2_ID" MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 CACHE 20;

-- ----------------------------
-- Primary Key structure for table course
-- ----------------------------
ALTER TABLE "DB_STUDENT"."course" ADD CONSTRAINT "SYS_C0012315" PRIMARY KEY ("id");

-- ----------------------------
-- Checks structure for table course
-- ----------------------------
ALTER TABLE "DB_STUDENT"."course" ADD CONSTRAINT "SYS_C0011211" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_STUDENT"."course" ADD CONSTRAINT "SYS_C0012303" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;

-- ----------------------------
-- Primary Key structure for table student
-- ----------------------------
ALTER TABLE "DB_STUDENT"."student" ADD CONSTRAINT "SYS_C0012316" PRIMARY KEY ("id");

-- ----------------------------
-- Checks structure for table student
-- ----------------------------
ALTER TABLE "DB_STUDENT"."student" ADD CONSTRAINT "SYS_C0011212" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_STUDENT"."student" ADD CONSTRAINT "SYS_C0012304" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;

-- ----------------------------
-- Primary Key structure for table student_course
-- ----------------------------
ALTER TABLE "DB_STUDENT"."student_course" ADD CONSTRAINT "SYS_C0012317" PRIMARY KEY ("id");

-- ----------------------------
-- Checks structure for table student_course
-- ----------------------------
ALTER TABLE "DB_STUDENT"."student_course" ADD CONSTRAINT "SYS_C0011213" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_STUDENT"."student_course" ADD CONSTRAINT "SYS_C0012305" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;

-- ----------------------------
-- Primary Key structure for table student_many_courses
-- ----------------------------
ALTER TABLE "DB_STUDENT"."student_many_courses" ADD CONSTRAINT "SYS_C0012318" PRIMARY KEY ("id");

-- ----------------------------
-- Checks structure for table student_many_courses
-- ----------------------------
ALTER TABLE "DB_STUDENT"."student_many_courses" ADD CONSTRAINT "SYS_C0011214" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_STUDENT"."student_many_courses" ADD CONSTRAINT "SYS_C0012306" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;

-- ----------------------------
-- Checks structure for table t1
-- ----------------------------
ALTER TABLE "DB_STUDENT"."t1" ADD CONSTRAINT "SYS_C0011215" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_STUDENT"."t1" ADD CONSTRAINT "SYS_C0012307" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;

-- ----------------------------
-- Checks structure for table t2
-- ----------------------------
ALTER TABLE "DB_STUDENT"."t2" ADD CONSTRAINT "SYS_C0011216" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_STUDENT"."t2" ADD CONSTRAINT "SYS_C0012308" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
