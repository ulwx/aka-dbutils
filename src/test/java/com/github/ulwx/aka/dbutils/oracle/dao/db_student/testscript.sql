
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
insert into "t1" ( "id", "a", "key_b", "key_c" )
values
    ( 1, 3, to_date( '2019-11-01 17:29:36', 'yyyy-mm-dd hh24:mi:ss' ), 'bbb' );
insert into "t1" ( "id", "a", "key_b", "key_c" )
values
    ( 2, 2, to_date( '2019-11-21 17:29:38', 'yyyy-mm-dd hh24:mi:ss' ), 'xxxx' );
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

insert into "t2" ( "id", "a", "key_a", "key_b" )
values
    ( 1, 1, 1, 0 );
insert into "t2" ( "id", "a", "key_a", "key_b" )
values
    ( 2, 1, 1, 6 );
insert into "t2" ( "id", "a", "key_a", "key_b" )
values
    ( 3, 1, 2, 0 );
insert into "t2" ( "id", "a", "key_a", "key_b" )
values
    ( 4, 1, 3, 1 );
insert into "t2" ( "id", "a", "key_a", "key_b" )
values
    ( 5, 1, 4, 1 );
insert into "t2" ( "id", "a", "key_a", "key_b" )
values
    ( 6, 2, 1, 2 );
insert into "t2" ( "id", "a", "key_a", "key_b" )
values
    ( 7, 2, 2, 4 );
insert into "t2" ( "id", "a", "key_a", "key_b" )
values
    ( 8, 3, 12, 44 );

select * from "t2" t;

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

----------------------------
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
-- Checks structure for table t1
-- ----------------------------
ALTER TABLE "DB_STUDENT"."t1" ADD CONSTRAINT "SYS_C0011215" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_STUDENT"."t1" ADD CONSTRAINT "SYS_C0012307" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;

-- ----------------------------
-- Checks structure for table t2
-- ----------------------------
ALTER TABLE "DB_STUDENT"."t2" ADD CONSTRAINT "SYS_C0011216" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
ALTER TABLE "DB_STUDENT"."t2" ADD CONSTRAINT "SYS_C0012308" CHECK ("id" IS NOT NULL) NOT DEFERRABLE INITIALLY IMMEDIATE NORELY VALIDATE;
