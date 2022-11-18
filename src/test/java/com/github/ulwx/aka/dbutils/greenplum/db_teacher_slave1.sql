
-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS "public"."teacher";
CREATE TABLE "public"."teacher" (
  "id" int4 NOT NULL ,
  "name" varchar(30) ,
  PRIMARY KEY ("id")
)
;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO "public"."teacher" VALUES (1, 'liyi');
INSERT INTO "public"."teacher" VALUES (2, 'leiming');
INSERT INTO "public"."teacher" VALUES (3, 'sunquan');
INSERT INTO "public"."teacher" VALUES (4, 'futao');

-- ----------------------------
-- sequence
-- ----------------------------
CREATE SEQUENCE "public"."teacher_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

SELECT setval('"public"."teacher_id_seq"', 4, true);

ALTER SEQUENCE "public"."teacher_id_seq"
OWNED BY "public"."teacher"."id";

ALTER SEQUENCE "public"."teacher_id_seq" OWNER TO  "gpadmin";
alter table "public"."teacher" alter column "id" set default nextval('"public"."teacher_id_seq"');
