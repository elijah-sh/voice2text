/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50724
Source Host           : 127.0.0.1:3306
Source Database       : voice2text

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2019-03-24 00:02:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `text`
-- ----------------------------
DROP TABLE IF EXISTS `text`;
CREATE TABLE `text` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bg` varchar(255) DEFAULT NULL,
  `ed` varchar(255) DEFAULT NULL,
  `onebest` varchar(1500) DEFAULT NULL,
  `si` varchar(255) DEFAULT NULL,
  `speaker` varchar(255) DEFAULT NULL,
  `words_result_sum` int(11) DEFAULT NULL,
  `date_time` datetime DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1731 DEFAULT CHARSET=utf32;


-- ----------------------------
-- Table structure for `voice_text_file`
-- ----------------------------
DROP TABLE IF EXISTS `voice_text_file`;
CREATE TABLE `voice_text_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title_name` varchar(255) DEFAULT NULL,
  `voice_name` varchar(255) DEFAULT NULL,
  `text_name` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `voice_path` varchar(255) DEFAULT NULL,
  `text_path` varchar(255) DEFAULT NULL,
  `file_size` varchar(255) DEFAULT NULL,
  `data_count` varchar(500) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19349 DEFAULT CHARSET=utf32;


-- ----------------------------
-- Table structure for `words_result`
-- ----------------------------
DROP TABLE IF EXISTS `words_result`;
CREATE TABLE `words_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alter_native` varchar(255) DEFAULT NULL,
  `wc` varchar(255) DEFAULT NULL,
  `wordBg` varchar(255) DEFAULT NULL,
  `wordEd` varchar(255) DEFAULT NULL,
  `wordsName` varchar(500) DEFAULT NULL,
  `wp` varchar(255) DEFAULT NULL,
  `text_id` int(11) DEFAULT NULL,
  `date_time` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20440 DEFAULT CHARSET=utf32;

