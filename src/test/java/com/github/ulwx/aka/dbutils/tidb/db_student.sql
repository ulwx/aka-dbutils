/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.25-TiDB-v6.3.0 : Database - db_student
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db_student` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `db_student`;

/*Table structure for table `course` */

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '课程id',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' COMMENT '课程名称',
  `class_hours` int(11) DEFAULT '0' COMMENT '学时',
  `teacher_id` int(11) DEFAULT '0' COMMENT '对应于db_teacher数据库里的teacher表',
  `creatime` datetime DEFAULT NULL COMMENT '建立时间',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30022 COMMENT='课程';

/*Data for the table `course` */

insert  into `course`(`id`,`name`,`class_hours`,`teacher_id`,`creatime`) values 
(1,'course1',11,1,'2021-03-15 22:31:48'),
(2,'course2',12,2,'2021-03-15 22:31:48'),
(3,'course3',13,3,'2021-03-15 22:31:48'),
(4,'course4',10,4,'2021-03-15 22:31:48'),
(5,'course5',11,1,'2021-03-15 22:31:48'),
(6,'course6',12,1,'2021-03-15 22:31:48'),
(7,'course7',13,2,'2021-03-15 22:31:48'),
(8,'course8',14,2,'2021-03-15 22:31:48'),
(9,'course9',15,3,'2021-03-15 22:31:48'),
(10,'course10',16,4,'2021-03-15 22:31:48'),
(11,'course11',17,1,'2021-03-15 22:31:48'),
(12,'course12',18,0,'2021-03-15 22:31:48'),
(13,'course13',19,0,'2021-03-15 22:31:48'),
(14,'course14',20,2,'2021-03-15 22:31:48'),
(15,'course15',21,1,'2021-03-15 22:31:48'),
(16,'course16',22,2,'2021-03-15 22:31:48'),
(17,'course17',23,4,'2021-03-15 22:31:48'),
(18,'course18',24,1,'2021-03-15 22:31:48'),
(19,'course19',25,0,'2021-03-15 22:31:48'),
(20,'course20',26,0,'2021-03-15 22:31:48'),
(21,'course21',27,0,'2021-03-15 22:31:48');

/*Table structure for table `student` */

DROP TABLE IF EXISTS `student`;

CREATE TABLE `student` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '学生id',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' COMMENT '学生姓名',
  `age` int(11) DEFAULT '0' COMMENT '年龄',
  `birth_day` date DEFAULT NULL COMMENT '出生日期',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30014 COMMENT='学生';

/*Data for the table `student` */

insert  into `student`(`id`,`name`,`age`,`birth_day`) values 
(1,'student1',40,'1980-10-08'),
(2,'student2',39,'1981-11-01'),
(3,'student3',38,'1982-10-08'),
(4,'student4',38,'1982-05-08'),
(5,'student5',38,'1982-06-08'),
(6,'student6',38,'1982-07-08'),
(7,'student7',38,'1982-03-08'),
(8,'student8',38,'1982-04-08'),
(9,'student9',38,'1982-06-08'),
(10,'student10',38,'1982-04-08'),
(11,'student11',38,'1982-06-08'),
(12,'student12',38,'1982-07-08'),
(13,'student13',38,'1982-01-08');

/*Table structure for table `student_course` */

DROP TABLE IF EXISTS `student_course`;

CREATE TABLE `student_course` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `student_id` int(11) DEFAULT NULL COMMENT '学生id',
  `course_id` int(11) DEFAULT NULL COMMENT '课程id',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30012;

/*Data for the table `student_course` */

insert  into `student_course`(`id`,`student_id`,`course_id`) values 
(1,1,10),
(2,2,13),
(3,3,14),
(4,4,15),
(5,5,12),
(6,6,16),
(7,7,15),
(8,8,12),
(9,9,14),
(10,10,16),
(11,11,20);

/*Table structure for table `student_many_courses` */

DROP TABLE IF EXISTS `student_many_courses`;

CREATE TABLE `student_many_courses` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `student_id` int(11) DEFAULT NULL COMMENT '学生id',
  `course_id` int(11) DEFAULT NULL COMMENT '课程id',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30013;

/*Data for the table `student_many_courses` */

insert  into `student_many_courses`(`id`,`student_id`,`course_id`) values 
(1,1,10),
(2,1,13),
(3,1,13),
(4,4,15),
(5,4,12),
(6,6,16),
(7,7,15),
(8,7,12),
(9,9,14),
(10,9,16),
(11,9,20),
(12,10,11);

/*Table structure for table `t1` */

DROP TABLE IF EXISTS `t1`;

CREATE TABLE `t1` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `a` int(11) DEFAULT NULL,
  `key_b` datetime DEFAULT NULL,
  `key_c` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

/*Data for the table `t1` */

/*Table structure for table `t2` */

DROP TABLE IF EXISTS `t2`;

CREATE TABLE `t2` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `a` int(11) DEFAULT NULL,
  `key_a` int(11) DEFAULT '0',
  `key_b` int(11) DEFAULT '0',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

/*Data for the table `t2` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
