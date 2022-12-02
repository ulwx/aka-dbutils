DROP TABLE IF EXISTS `teacher`;

CREATE TABLE `teacher` (
  `id` int ,
  `name` varchar(30)
)
    STORED AS ORC
    TBLPROPERTIES ("transactional"="true");


insert  into `teacher`(`id`,`name`) values 
(1,'liyi'),
(2,'leiming'),
(3,'sunquan'),
(4,'futao');


