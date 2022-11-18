

-- ----------------------------
-- Table structure for course
-- ----------------------------


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
insert into t1(id, a, key_b, key_c)
values (1, 3, '2019-11-01 17:29:36', 'bbb'),
       (2, 2, '2019-11-21 17:29:38', 'xxxx');
-- ----------------------------
-- Table structure for t2
-- ----------------------------
DROP TABLE IF EXISTS "public"."t2";
CREATE TABLE "public"."t2" (
   "id" int4 NOT NULL,
   "a" int4,
   "key_a" int4,
   "key_b" int4,
   PRIMARY KEY ("id")
)
;

insert into t2(id, a, key_a, key_b)
values (1, 1, 1, 0),
       (2, 1, 1, 6),
       (3, 1, 2, 0),
       (4, 1, 3, 1),
       (5, 1, 4, 1),
       (6, 2, 1, 2),
       (7, 2, 2, 4),
       (8, 3, 12, 44);
select * from t2 t;
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
-- Alter sequences owned by
-- ----------------------------
--  -------------------
CREATE SEQUENCE "public"."t1_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

SELECT setval('"public"."t1_id_seq"',  max(id), false) from t1;
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

SELECT setval('"public"."t2_id_seq"',  max(id), false) from t2;
ALTER SEQUENCE "public"."t2_id_seq"
OWNED BY "public"."t2"."id";
ALTER SEQUENCE "public"."t2_id_seq" OWNER TO  "gpadmin";
alter table "public"."t2" alter column "id" set default nextval('"public"."t2_id_seq"');







