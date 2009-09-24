/*
MySQL Data Transfer
Source Host: localhost
Source Database: nbs
Target Host: localhost
Target Database: nbs
Date: 3/4/2008 11:19:54 AM
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for nbs_alert
-- ----------------------------
CREATE TABLE `nbs_alert` (
  `nbs_alert_id` int(11) NOT NULL auto_increment,
  `alert_id` int(11) NOT NULL,
  `form_id` int(11) NOT NULL,
  `patient_id` int(11) default NULL,
  `encounter_id` int(11) default NULL,
  `mrn` varchar(20) default NULL,
  `provider` varchar(100) default NULL,
  `retired` tinyint(1) NOT NULL,
  `alert` text,
  `datestamp` datetime default NULL,
  `Record_Sta` varchar(20) default NULL,
  `Time_Stamp` varchar(20) default NULL,
  `status` int(11) default '0',
  PRIMARY KEY  (`nbs_alert_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `patient_id` FOREIGN KEY (`patient_id`) REFERENCES `patient_identifier` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

