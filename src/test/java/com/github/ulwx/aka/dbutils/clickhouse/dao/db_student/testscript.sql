

DROP TABLE IF EXISTS `t1`;

CREATE TABLE `t1`
(
    `id`    int(10) unsigned NOT NULL ,
    `a`     int(11)     DEFAULT NULL,
    `key_b` datetime    DEFAULT NULL,
    `key_c` varchar(30) DEFAULT '',
    PRIMARY KEY (`id`)
) ENGINE = MergeTree() ;

/*Data for the table `t1` */

insert into `t1`(`id`, `a`, `key_b`, `key_c`)
values (1, 3, '2019-11-01 17:29:36', 'bbb'),
       (2, 2, '2019-11-21 17:29:38', 'xxxx');

/*Table structure for table `t2` */

DROP TABLE IF EXISTS `t2`;

CREATE TABLE `t2`
(
    `id`    int(10) unsigned NOT NULL,
    `a`     int(11) DEFAULT NULL,
    `key_a` int(11) DEFAULT '0',
    `key_b` int(11) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = MergeTree() ;

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



