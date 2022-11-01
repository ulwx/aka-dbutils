/*
 Navicat Premium Data Transfer

 Source Server         : SQLLlite_db_teacher_slave1
 Source Server Type    : SQLite
 Source Server Version : 3035005 (3.35.5)
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3035005 (3.35.5)
 File Encoding         : 65001

 Date: 28/10/2022 17:58:53
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS "teacher";
CREATE TABLE "teacher" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "name" text(30)
);

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO "teacher" VALUES (1, 'liyi');
INSERT INTO "teacher" VALUES (2, 'leiming');
INSERT INTO "teacher" VALUES (3, 'sunquan');
INSERT INTO "teacher" VALUES (4, 'futao');

PRAGMA foreign_keys = true;
