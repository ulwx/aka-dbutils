/*
 Navicat Premium Data Transfer

 Source Server         : sqlserver
 Source Server Type    : SQL Server
 Source Server Version : 13004001 (13.00.4001)
 Source Host           : 192.168.137.200:1433
 Source Catalog        : db_teacher_slave1
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 13004001 (13.00.4001)
 File Encoding         : 65001

 Date: 10/10/2022 15:08:30
*/


-- ----------------------------
-- Table structure for teacher
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[teacher]') AND type IN ('U'))
	DROP TABLE [dbo].[teacher]
GO

CREATE TABLE [dbo].[teacher] (
  [id] int  NOT NULL,
  [name] nvarchar(30) COLLATE Chinese_PRC_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[teacher] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO [dbo].[teacher] ([id], [name]) VALUES (N'1', N'liyi')
GO

INSERT INTO [dbo].[teacher] ([id], [name]) VALUES (N'2', N'leiming')
GO

INSERT INTO [dbo].[teacher] ([id], [name]) VALUES (N'3', N'sunquan')
GO

INSERT INTO [dbo].[teacher] ([id], [name]) VALUES (N'4', N'futao')
GO


-- ----------------------------
-- Primary Key structure for table teacher
-- ----------------------------
ALTER TABLE [dbo].[teacher] ADD CONSTRAINT [PK__teacher__3213E83FCF6FF9D7] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO

