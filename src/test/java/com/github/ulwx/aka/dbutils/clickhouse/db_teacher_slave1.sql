
CREATE DATABASE IF NOT EXISTS db_teacher_slave2 ;

/*Table structure for table `teacher` */

DROP TABLE IF EXISTS `teacher`;

CREATE TABLE `teacher` (
                           `id` int(10) unsigned NOT NULL ,
                           `name` varchar(30) NULL DEFAULT '',
                           PRIMARY KEY (`id`)
) ENGINE=MergeTree() ;

/*Data for the table `teacher` */

insert  into `teacher`(`id`,`name`) values
                                        (1,'abcd2'),
                                        (2,'leiming'),
                                        (3,'sunquan'),
                                        (4,'futao');

