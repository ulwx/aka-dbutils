

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
drop table  if exists  "teacher";
CREATE TABLE "teacher" (
  "id"  INTEGER generated by default as identity,
  "name" varchar(30),
  PRIMARY KEY("id")
);

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO "teacher" VALUES (1, 'liyi');
INSERT INTO "teacher" VALUES (2, 'leiming');
INSERT INTO "teacher" VALUES (3, 'sunquan');
INSERT INTO "teacher" VALUES (4, 'futao');

ALTER TABLE "teacher" ALTER COLUMN "id" RESTART WITH 5;
