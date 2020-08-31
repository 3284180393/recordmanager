/*
Navicat SQLite Data Transfer

Source Server         : recordmanager
Source Server Version : 30808
Source Host           : :0

Target Server Type    : SQLite
Target Server Version : 30808
File Encoding         : 65001

Date: 2020-08-31 16:48:02
*/

PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for bak_record_index
-- ----------------------------
DROP TABLE IF EXISTS "main"."bak_record_index";
CREATE TABLE "bak_record_index" (
"RECORD_NAME"  VARCHAR(128) NOT NULL,
"ENT_ID"  VARCHAR(64) NOT NULL,
"SESSION_ID"  VARCHAR(32),
"REMOTE_URI"  VARCHAR(32),
"LOCAL_URI"  VARCHAR(32),
"AGENT_ID"  VARCHAR(32),
"CMS_NAME"  VARCHAR(32),
"CALL_TYPE"  smallint,
"DEVICE_NUMBER"  VARCHAR(32) NOT NULL,
"START_TIME"  datetime,
"END_TIME"  datetime,
"CTI_END_TIME"  datetime,
"CTI_START_TIME"  datetime,
"SKILL_NAME"  VARCHAR(1024),
PRIMARY KEY ("RECORD_NAME" ASC)
);
