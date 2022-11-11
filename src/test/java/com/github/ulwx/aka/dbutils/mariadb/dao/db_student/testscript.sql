/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.31-log : Database - testa
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE = ''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;


/*Table structure for table `t1` */

DROP TABLE IF EXISTS `t1`;

CREATE TABLE `t1`
(
    `id`    int(10) unsigned NOT NULL AUTO_INCREMENT,
    `a`     int(11)     DEFAULT NULL,
    `key_b` datetime    DEFAULT NULL,
    `key_c` varchar(30) DEFAULT '',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8;

/*Data for the table `t1` */

insert into `t1`(`id`, `a`, `key_b`, `key_c`)
values (1, 3, '2019-11-01 17:29:36', 'bbb'),
       (2, 2, '2019-11-21 17:29:38', 'xxxx');

/*Table structure for table `t2` */

DROP TABLE IF EXISTS `t2`;

CREATE TABLE `t2`
(
    `id`    int(10) unsigned NOT NULL AUTO_INCREMENT,
    `a`     int(11) DEFAULT NULL,
    `key_a` int(11) DEFAULT '0',
    `key_b` int(11) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARSET = utf8;

/*Data for the table `t2` */

insert into `t2`(`id`, `a`, `key_a`, `key_b`)
values (1, 1, 1, 0),
       (2, 1, 1, 6),
       (3, 1, 2, 0),
       (4, 1, 3, 1),
       (5, 1, 4, 1),
       (6, 2, 1, 2),
       (7, 2, 2, 4),
       (8, 3, 12, 44);

select *
from t2 t;
/* Procedure structure for procedure `aaa` */

/*!50003 DROP PROCEDURE IF EXISTS `testproc` */;

DELIMITER $$

/*!50003 CREATE
    DEFINER = `root`@`%` PROCEDURE `testproc`()
BEGIN
    set @i = 1;
    select 1 into @i from t1;
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

