/*
SQLyog Ultimate
MySQL - 5.7.19-log : Database - testb
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`testb` /*!40100 DEFAULT CHARACTER SET utf8 */;

/*Table structure for table `T1` */

DROP TABLE IF EXISTS `T1`;

CREATE TABLE `T1` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `a` int(11) DEFAULT NULL,
  `key_b` datetime DEFAULT NULL,
  `key_c` varchar(30) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `T1` */

insert  into `T1`(`id`,`a`,`key_b`,`key_c`) values 
(1,3,'2019-11-02 11:08:41','77'),
(2,2,'2019-11-20 11:08:43','88');

/*Table structure for table `T2` */

DROP TABLE IF EXISTS `T2`;

CREATE TABLE `T2` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `a` int(11) DEFAULT NULL,
  `key_a` int(11) DEFAULT '0',
  `key_b` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

/*Data for the table `T2` */

insert  into `T2`(`id`,`a`,`key_a`,`key_b`) values 
(1,1,1,0),
(2,1,1,6),
(3,1,2,0),
(4,1,3,1),
(5,1,4,1),
(6,2,1,2),
(7,2,2,4),
(8,3,12,44);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
