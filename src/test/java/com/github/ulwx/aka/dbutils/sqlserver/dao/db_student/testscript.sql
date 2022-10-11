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

 Date: 10/10/2022 17:38:54
*/



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
insert into t1(id, a, key_b, key_c)
    values (1, 3, '2019-11-01 17:29:36', 'bbb'),
           (2, 2, '2019-11-21 17:29:38', 'xxxx')
        GO
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
insert into t2(id, a, key_a, key_b)
values (1, 1, 1, 0),
       (2, 1, 1, 6),
       (3, 1, 2, 0),
       (4, 1, 3, 1),
       (5, 1, 4, 1),
       (6, 2, 1, 2),
       (7, 2, 2, 4),
       (8, 3, 12, 44)
    GO
select * from t2 t
    GO



-- ----------------------------
-- procedure structure for query_course_proc
-- ----------------------------

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

