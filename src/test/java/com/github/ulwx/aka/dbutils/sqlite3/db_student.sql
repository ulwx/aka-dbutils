/*
 Navicat Premium Data Transfer

 Source Server         : SQLlite_db_student
 Source Server Type    : SQLite
 Source Server Version : 3035005 (3.35.5)
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3035005 (3.35.5)
 File Encoding         : 65001

 Date: 28/10/2022 17:58:27
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS "course";
CREATE TABLE "course" (
   "id"  INTEGER PRIMARY KEY AUTOINCREMENT,
  "name" text(20),
  "class_hours" integer,
  "teacher_id" integer,
  "creatime" text
);

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO "course" VALUES (1, 'course1', 11, 1, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (2, 'course2', 12, 2, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (3, 'course3', 13, 3, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (4, 'course4', 10, 4, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (5, 'course5', 11, 1, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (6, 'course6', 12, 1, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (7, 'course7', 13, 2, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (8, 'course8', 14, 2, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (9, 'course9', 15, 3, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (10, 'course10', 16, 4, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (11, 'course11', 17, 1, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (12, 'course12', 18, 0, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (13, 'course13', 19, 0, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (14, 'course14', 20, 2, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (15, 'course15', 21, 1, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (16, 'course16', 22, 2, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (17, 'course17', 23, 4, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (18, 'course18', 24, 1, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (19, 'course19', 25, 0, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (20, 'course20', 26, 0, '2021-03-15 22:31:48');
INSERT INTO "course" VALUES (21, 'course21', 27, 0, '2021-03-15 22:31:48');


-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS "student";
CREATE TABLE "student" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "name" text(20),
  "age" integer,
  "birth_day" text
);

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO "student" VALUES (1, 'student1', 40, '1980-10-08');
INSERT INTO "student" VALUES (2, 'student2', 39, '1981-11-01');
INSERT INTO "student" VALUES (3, 'student3', 38, '1982-10-08');
INSERT INTO "student" VALUES (4, 'student4', 38, '1982-05-08');
INSERT INTO "student" VALUES (5, 'student5', 38, '1982-06-08');
INSERT INTO "student" VALUES (6, 'student6', 38, '1982-07-08');
INSERT INTO "student" VALUES (7, 'student7', 38, '1982-03-08');
INSERT INTO "student" VALUES (8, 'student8', 38, '1982-04-08');
INSERT INTO "student" VALUES (9, 'student9', 38, '1982-06-08');
INSERT INTO "student" VALUES (10, 'student10', 38, '1982-04-08');
INSERT INTO "student" VALUES (11, 'student11', 38, '1982-06-08');
INSERT INTO "student" VALUES (12, 'student12', 38, '1982-07-08');
INSERT INTO "student" VALUES (13, 'student13', 38, '1982-01-08');

-- ----------------------------
-- Table structure for student_course
-- ----------------------------
DROP TABLE IF EXISTS "student_course";
CREATE TABLE "student_course" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "student_id" integer,
  "course_id" integer
);

-- ----------------------------
-- Records of student_course
-- ----------------------------
INSERT INTO "student_course" VALUES (1, 1, 10);
INSERT INTO "student_course" VALUES (2, 2, 13);
INSERT INTO "student_course" VALUES (3, 3, 14);
INSERT INTO "student_course" VALUES (4, 4, 15);
INSERT INTO "student_course" VALUES (5, 5, 12);
INSERT INTO "student_course" VALUES (6, 6, 16);
INSERT INTO "student_course" VALUES (7, 7, 15);
INSERT INTO "student_course" VALUES (8, 8, 12);
INSERT INTO "student_course" VALUES (9, 9, 14);
INSERT INTO "student_course" VALUES (10, 10, 16);
INSERT INTO "student_course" VALUES (11, 11, 20);

-- ----------------------------
-- Table structure for student_many_courses
-- ----------------------------
DROP TABLE IF EXISTS "student_many_courses";
CREATE TABLE "student_many_courses" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "student_id" integer,
  "course_id" integer
);

-- ----------------------------
-- Records of student_many_courses
-- ----------------------------
INSERT INTO "student_many_courses" VALUES (1, 1, 10);
INSERT INTO "student_many_courses" VALUES (2, 1, 13);
INSERT INTO "student_many_courses" VALUES (3, 1, 13);
INSERT INTO "student_many_courses" VALUES (4, 4, 15);
INSERT INTO "student_many_courses" VALUES (5, 4, 12);
INSERT INTO "student_many_courses" VALUES (6, 6, 16);
INSERT INTO "student_many_courses" VALUES (7, 7, 15);
INSERT INTO "student_many_courses" VALUES (8, 7, 12);
INSERT INTO "student_many_courses" VALUES (9, 9, 14);
INSERT INTO "student_many_courses" VALUES (10, 9, 16);
INSERT INTO "student_many_courses" VALUES (11, 9, 20);
INSERT INTO "student_many_courses" VALUES (12, 10, 11);

-- ----------------------------
-- Table structure for t1
-- ----------------------------
DROP TABLE IF EXISTS "t1";
CREATE TABLE "t1" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "a" integer,
  "key_b" text,
  "key_c" text(30)
);

-- ----------------------------
-- Records of t1
-- ----------------------------

-- ----------------------------
-- Table structure for t2
-- ----------------------------
DROP TABLE IF EXISTS "t2";
CREATE TABLE "t2" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "a" integer,
  "key_a" integer,
  "key_b" integer
);

-- ----------------------------
-- Records of t2
-- ----------------------------

PRAGMA foreign_keys = true;
