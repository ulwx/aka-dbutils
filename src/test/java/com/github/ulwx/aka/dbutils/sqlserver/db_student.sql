/*
 Navicat Premium Data Transfer

 Source Server         : sqlserver
 Source Server Type    : SQL Server
 Source Server Version : 13004001 (13.00.4001)
 Source Host           : 192.168.137.200:1433
 Source Catalog        : db_student
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 13004001 (13.00.4001)
 File Encoding         : 65001

 Date: 10/10/2022 15:07:00
*/


-- ----------------------------
-- Table structure for course
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[course]') AND type IN ('U'))
	DROP TABLE [dbo].[course]
GO

CREATE TABLE [dbo].[course] (
  [id] int  NOT NULL,
  [name] nvarchar(20) COLLATE Chinese_PRC_CI_AS  NULL,
  [class_hours] int  NULL,
  [teacher_id] int  NULL,
  [creatime] datetime2(7)  NULL
)
GO

ALTER TABLE [dbo].[course] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'课程id',
'SCHEMA', N'dbo',
'TABLE', N'course',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'课程名称',
'SCHEMA', N'dbo',
'TABLE', N'course',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'学时',
'SCHEMA', N'dbo',
'TABLE', N'course',
'COLUMN', N'class_hours'
GO

EXEC sp_addextendedproperty
'MS_Description', N'对应于db_teacher数据库里的teacher表',
'SCHEMA', N'dbo',
'TABLE', N'course',
'COLUMN', N'teacher_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'建立时间',
'SCHEMA', N'dbo',
'TABLE', N'course',
'COLUMN', N'creatime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'课程',
'SCHEMA', N'dbo',
'TABLE', N'course'
GO


-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'1', N'abcd1', N'11', N'1', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'2', N'course2', N'12', N'2', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'3', N'course3', N'13', N'3', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'4', N'course4', N'10', N'4', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'5', N'course5', N'11', N'1', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'6', N'course6', N'12', N'1', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'7', N'course7', N'13', N'2', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'8', N'course8', N'14', N'2', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'9', N'course9', N'15', N'3', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'10', N'course10', N'16', N'4', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'11', N'course11', N'17', N'1', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'12', N'course12', N'18', N'0', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'13', N'course13', N'19', N'0', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'14', N'course14', N'20', N'2', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'15', N'course15', N'21', N'1', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'16', N'course16', N'22', N'2', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'17', N'course17', N'23', N'4', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'18', N'course18', N'24', N'1', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'19', N'course19', N'25', N'0', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'20', N'course20', N'26', N'0', N'2021-03-15 22:31:48.0000000')
GO

INSERT INTO [dbo].[course] ([id], [name], [class_hours], [teacher_id], [creatime]) VALUES (N'21', N'course21', N'27', N'0', N'2021-03-15 22:31:48.0000000')
GO


-- ----------------------------
-- Table structure for student
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[student]') AND type IN ('U'))
	DROP TABLE [dbo].[student]
GO

CREATE TABLE [dbo].[student] (
  [id] int  NOT NULL,
  [name] nvarchar(20) COLLATE Chinese_PRC_CI_AS  NULL,
  [age] int  NULL,
  [birth_day] date  NULL
)
GO

ALTER TABLE [dbo].[student] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'学生id',
'SCHEMA', N'dbo',
'TABLE', N'student',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'学生姓名',
'SCHEMA', N'dbo',
'TABLE', N'student',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'年龄',
'SCHEMA', N'dbo',
'TABLE', N'student',
'COLUMN', N'age'
GO

EXEC sp_addextendedproperty
'MS_Description', N'出生日期',
'SCHEMA', N'dbo',
'TABLE', N'student',
'COLUMN', N'birth_day'
GO

EXEC sp_addextendedproperty
'MS_Description', N'学生',
'SCHEMA', N'dbo',
'TABLE', N'student'
GO


-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'1', N'student1', N'40', N'1980-10-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'2', N'student2', N'39', N'1981-11-01')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'3', N'student3', N'38', N'1982-10-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'4', N'student4', N'38', N'1982-05-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'5', N'student5', N'38', N'1982-06-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'6', N'student6', N'38', N'1982-07-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'7', N'student7', N'38', N'1982-03-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'8', N'student8', N'38', N'1982-04-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'9', N'student9', N'38', N'1982-06-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'10', N'student10', N'38', N'1982-04-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'11', N'student11', N'38', N'1982-06-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'12', N'student12', N'38', N'1982-07-08')
GO

INSERT INTO [dbo].[student] ([id], [name], [age], [birth_day]) VALUES (N'13', N'student13', N'38', N'1982-01-08')
GO


-- ----------------------------
-- Table structure for student_course
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[student_course]') AND type IN ('U'))
	DROP TABLE [dbo].[student_course]
GO

CREATE TABLE [dbo].[student_course] (
  [id] int  NOT NULL,
  [student_id] int  NULL,
  [course_id] int  NULL
)
GO

ALTER TABLE [dbo].[student_course] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'学生id',
'SCHEMA', N'dbo',
'TABLE', N'student_course',
'COLUMN', N'student_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'课程id',
'SCHEMA', N'dbo',
'TABLE', N'student_course',
'COLUMN', N'course_id'
GO


-- ----------------------------
-- Records of student_course
-- ----------------------------
INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'1', N'1', N'10')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'2', N'2', N'13')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'3', N'3', N'14')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'4', N'4', N'15')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'5', N'5', N'12')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'6', N'6', N'16')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'7', N'7', N'15')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'8', N'8', N'12')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'9', N'9', N'14')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'10', N'10', N'16')
GO

INSERT INTO [dbo].[student_course] ([id], [student_id], [course_id]) VALUES (N'11', N'11', N'20')
GO


-- ----------------------------
-- Table structure for student_many_courses
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[student_many_courses]') AND type IN ('U'))
	DROP TABLE [dbo].[student_many_courses]
GO

CREATE TABLE [dbo].[student_many_courses] (
  [id] int  NOT NULL,
  [student_id] int  NULL,
  [course_id] int  NULL
)
GO

ALTER TABLE [dbo].[student_many_courses] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'学生id',
'SCHEMA', N'dbo',
'TABLE', N'student_many_courses',
'COLUMN', N'student_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'课程id',
'SCHEMA', N'dbo',
'TABLE', N'student_many_courses',
'COLUMN', N'course_id'
GO


-- ----------------------------
-- Records of student_many_courses
-- ----------------------------
INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'1', N'1', N'10')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'2', N'1', N'13')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'3', N'1', N'13')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'4', N'4', N'15')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'5', N'4', N'12')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'6', N'6', N'16')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'7', N'7', N'15')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'8', N'7', N'12')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'9', N'9', N'14')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'10', N'9', N'16')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'11', N'9', N'20')
GO

INSERT INTO [dbo].[student_many_courses] ([id], [student_id], [course_id]) VALUES (N'12', N'10', N'11')
GO


-- ----------------------------
-- Table structure for t1
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[t1]') AND type IN ('U'))
	DROP TABLE [dbo].[t1]
GO

CREATE TABLE [dbo].[t1] (
  [id] int  NOT NULL,
  [a] int  NULL,
  [key_b] datetime2(7)  NULL,
  [key_c] nvarchar(30) COLLATE Chinese_PRC_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[t1] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of t1
-- ----------------------------

-- ----------------------------
-- Table structure for t2
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[t2]') AND type IN ('U'))
	DROP TABLE [dbo].[t2]
GO

CREATE TABLE [dbo].[t2] (
  [id] int  NOT NULL,
  [a] int  NULL,
  [key_a] int  NULL,
  [key_b] int  NULL
)
GO

ALTER TABLE [dbo].[t2] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of t2
-- ----------------------------

-- ----------------------------
-- Table structure for Table_1
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Table_1]') AND type IN ('U'))
	DROP TABLE [dbo].[Table_1]
GO

CREATE TABLE [dbo].[Table_1] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [name] varchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [date1] datetime  NULL
)
GO

ALTER TABLE [dbo].[Table_1] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'abcggggg',
'SCHEMA', N'dbo',
'TABLE', N'Table_1',
'COLUMN', N'id'
GO


-- ----------------------------
-- Records of Table_1
-- ----------------------------
SET IDENTITY_INSERT [dbo].[Table_1] ON
GO

SET IDENTITY_INSERT [dbo].[Table_1] OFF
GO


-- ----------------------------
-- function structure for query_course_cnt_func
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[query_course_cnt_func]') AND type IN ('FN', 'FS', 'FT', 'IF', 'TF'))
	DROP FUNCTION[dbo].[query_course_cnt_func]
GO

CREATE FUNCTION [dbo].[query_course_cnt_func](
  @v_name AS varchar(40)) RETURNS INT
AS
BEGIN
	declare @v_ret INT;
	SELECT  @v_ret=count(1) FROM course WHERE name = @v_name;
	RETURN @v_ret;
END
GO


-- ----------------------------
-- procedure structure for query_course_proc
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[query_course_proc]') AND type IN ('P', 'PC', 'RF', 'X'))
	DROP PROCEDURE[dbo].[query_course_proc]
GO

CREATE PROCEDURE [dbo].[query_course_proc]( @v_cname VARCHAR(45) , @v_cnt INT OUTPUT)
AS
BEGIN
    SELECT   @v_cnt=count(1)  FROM course ;    
    SELECT * FROM course WHERE name = @v_cname;
END
GO


-- ----------------------------
-- procedure structure for testproc
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[testproc]') AND type IN ('P', 'PC', 'RF', 'X'))
	DROP PROCEDURE[dbo].[testproc]
GO

CREATE PROCEDURE [dbo].[testproc]
AS
BEGIN
     declare @i INT;
	   set @i=1;
	   select  @i=sum(1) from t1 ;
	END
GO


-- ----------------------------
-- Primary Key structure for table course
-- ----------------------------
ALTER TABLE [dbo].[course] ADD CONSTRAINT [PK__course__3213E83F6DCB5080] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table student
-- ----------------------------
ALTER TABLE [dbo].[student] ADD CONSTRAINT [PK__student__3213E83FB4169C94] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table student_course
-- ----------------------------
ALTER TABLE [dbo].[student_course] ADD CONSTRAINT [PK__student___3213E83F14D97571] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table student_many_courses
-- ----------------------------
ALTER TABLE [dbo].[student_many_courses] ADD CONSTRAINT [PK__student___3213E83F873A6ABB] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table t1
-- ----------------------------
ALTER TABLE [dbo].[t1] ADD CONSTRAINT [PK__t1__3213E83F2B067CCC] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table t2
-- ----------------------------
ALTER TABLE [dbo].[t2] ADD CONSTRAINT [PK__t2__3213E83F9DAC7319] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for Table_1
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[Table_1]', RESEED, 1)
GO

