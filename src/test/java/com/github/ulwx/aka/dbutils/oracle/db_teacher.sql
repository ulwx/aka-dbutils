/*
 Navicat Premium Data Transfer

 Source Server         : oracle
 Source Server Type    : Oracle
 Source Server Version : 110200 (Oracle Database 11g Enterprise Edition Release 11.2.0.1.0 - 64bit Production
With the Partitioning, OLAP, Data Mining and Real Application Testing options)
 Source Host           : 192.168.137.200:1521
 Source Schema         : DB_TEACHER

 Target Server Type    : Oracle
 Target Server Version : 110200 (Oracle Database 11g Enterprise Edition Release 11.2.0.1.0 - 64bit Production
With the Partitioning, OLAP, Data Mining and Real Application Testing options)
 File Encoding         : 65001

 Date: 26/10/2022 13:49:51
*/


-- ----------------------------
-- Table structure for teacher
-- ----------------------------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE "DB_TEACHER"."teacher"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE TABLE "DB_TEACHER"."teacher" (
  "id" NUMBER(11,0) NOT NULL,
  "name" NVARCHAR2(30)
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

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO "DB_TEACHER"."teacher" VALUES ('1', 'liyi');
INSERT INTO "DB_TEACHER"."teacher" VALUES ('2', 'leiming');
INSERT INTO "DB_TEACHER"."teacher" VALUES ('3', 'sunquan');
INSERT INTO "DB_TEACHER"."teacher" VALUES ('4', 'futao');

-- ----------------------------
-- Sequence structure for SEQ_TEACHER_ID
-- ----------------------------

BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE "DB_TEACHER"."SEQ_TEACHER_ID"'; EXCEPTION WHEN OTHERS THEN NULL;END;
/
CREATE SEQUENCE "DB_TEACHER"."SEQ_TEACHER_ID" MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START with 5 CACHE 20;


-- Primary Key structure for table teacher
-- ----------------------------
ALTER TABLE "DB_TEACHER"."teacher" ADD CONSTRAINT "teacher_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Checks structure for table teacher
-- ----------------------------
ALTER TABLE "DB_TEACHER"."teacher" ADD CONSTRAINT "SYS_C0011083" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_TEACHER"."teacher" ADD CONSTRAINT "SYS_C0012258" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
