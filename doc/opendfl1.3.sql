/*
 Navicat MySQL Data Transfer

 Source Server         : hw
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 192.168.1.254:3306
 Source Schema         : opendfl

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 19/08/2022 21:12:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dfl_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `dfl_audit_log`;
CREATE TABLE `dfl_audit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uri_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `role_id` int NULL DEFAULT NULL,
  `ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户IP',
  `sys_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统类型',
  `oper_type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作类型',
  `attr_data` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `times` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE,
  INDEX `idx_user`(`user_id`) USING BTREE,
  INDEX `idx_uri`(`uri_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 520 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '后台管理审计日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for dfl_black_white
-- ----------------------------
DROP TABLE IF EXISTS `dfl_black_white`;
CREATE TABLE `dfl_black_white`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'black/white',
  `limit_type` tinyint NULL DEFAULT NULL COMMENT '1 user, 2 ip 3 deviceId 4 dataId',
  `code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `if_del` tinyint NOT NULL,
  `status` tinyint NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `modify_time` datetime NULL DEFAULT NULL,
  `create_user` int NULL DEFAULT NULL,
  `modify_user` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '黑白名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_black_white
-- ----------------------------
INSERT INTO `dfl_black_white` VALUES (1, 'white', 1, 'whiteUser', '用户白名单', NULL, 0, 1, NULL, '2022-05-21 22:40:12', NULL, -1);
INSERT INTO `dfl_black_white` VALUES (2, 'black', 1, 'blackUser', '用户黑名单', NULL, 0, 1, '2022-05-18 22:44:34', '2022-05-20 06:49:36', 2, 2);
INSERT INTO `dfl_black_white` VALUES (3, 'white', 2, 'whiteIp', 'IP白名单', NULL, 0, 1, '2022-05-20 06:50:43', '2022-05-20 06:52:08', 2, 2);
INSERT INTO `dfl_black_white` VALUES (4, 'black', 2, 'blackIp', 'IP黑名单', NULL, 0, 1, '2022-05-20 06:53:01', '2022-05-20 06:53:01', 2, 2);
INSERT INTO `dfl_black_white` VALUES (5, 'black', 3, 'blackDeviceId', '设备号黑名单', NULL, 0, 1, '2022-05-20 06:53:56', '2022-05-20 06:53:56', 2, 2);

-- ----------------------------
-- Table structure for dfl_black_white_item
-- ----------------------------
DROP TABLE IF EXISTS `dfl_black_white_item`;
CREATE TABLE `dfl_black_white_item`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `blackwhite_id` int NULL DEFAULT NULL,
  `data` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据',
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `status` tinyint NOT NULL COMMENT '状态:是否有效0无效，1有效',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '黑名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_black_white_item
-- ----------------------------
INSERT INTO `dfl_black_white_item` VALUES (1, 1, '5101', 0, 1, 'test', '2022-05-18 22:37:09', '2022-05-20 21:50:54', 2, 2);
INSERT INTO `dfl_black_white_item` VALUES (2, 1, '5102', 0, 1, 'aa', '2022-05-18 22:44:14', '2022-06-09 22:26:45', 2, 2);
INSERT INTO `dfl_black_white_item` VALUES (3, 2, '5103', 0, 1, NULL, '2022-05-18 23:35:13', '2022-06-09 22:22:52', 2, 2);
INSERT INTO `dfl_black_white_item` VALUES (4, 3, '192.168.5.101', 0, 1, NULL, '2022-05-20 06:52:22', '2022-05-20 06:52:22', 2, 2);
INSERT INTO `dfl_black_white_item` VALUES (5, 4, '192.168.5.103', 0, 1, NULL, '2022-05-20 06:53:11', '2022-06-18 00:03:56', 2, 2);
INSERT INTO `dfl_black_white_item` VALUES (6, 5, 'blackDevice123', 0, 1, NULL, '2022-05-20 06:54:01', '2022-06-09 22:32:20', 2, 2);

-- ----------------------------
-- Table structure for dfl_frequency
-- ----------------------------
DROP TABLE IF EXISTS `dfl_frequency`;
CREATE TABLE `dfl_frequency`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uri_id` int NULL DEFAULT NULL,
  `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `alias` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名，用于多个接口共用一个限制',
  `time` int NULL DEFAULT NULL COMMENT '时间间隔',
  `limit_count` int NULL DEFAULT NULL COMMENT '限制次数',
  `user_ip_count` int NULL DEFAULT NULL COMMENT '一个用户允许IP个数',
  `ip_user_count` int NULL DEFAULT NULL COMMENT '一个IP允许用户个数',
  `white_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '白名单编码',
  `limit_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '限制类型',
  `attr_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '限制属性名',
  `need_login` tinyint NULL DEFAULT NULL COMMENT '是否需要登入',
  `err_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常消息',
  `err_msg_en` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常消息en',
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `status` tinyint NOT NULL COMMENT '状态:是否有效0无效，1有效',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  `method` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `resource` tinyint NULL DEFAULT NULL,
  `freq_limit_type` tinyint NULL DEFAULT NULL,
  `log` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 243 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '频率限制配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_frequency
-- ----------------------------
INSERT INTO `dfl_frequency` VALUES (18, '/frequencyTest/serverTimeFreqIp', 18, 'serverTimeFreqIp', NULL, '', 5, 5, 7, 7, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-05-19 21:24:17', '2022-05-19 21:24:17', NULL, NULL, NULL, NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (19, '/frequencyTest/serverTimeFreq', 14, 'serverTimeFreq', NULL, '', 5, 3, 0, 0, 'none', 'frequency', '', 0, '每#{time}秒#{limit}次', '#{limit} times every #{time}s', 0, 1, '2022-05-19 21:24:23', '2022-08-13 07:24:32', NULL, 2, NULL, 'test', NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (20, '/frequencyTest/serverTimeFreq', 14, 'serverTimeFreq', NULL, '', 3600, 100, 0, 0, 'none', 'frequency2', '', 0, '', '', 0, 1, '2022-05-19 21:24:23', '2022-08-13 07:24:33', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (21, '/frequencyTest/waitLockTestOrder', 20, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-19 21:24:28', '2022-05-19 21:24:28', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (22, '/frequencyTest/serverTimeNeedLogin', 19, 'serverTimeNeedLogin', NULL, '', 5, 5, 0, 0, 'none', 'frequency', '', 1, '', '', 0, 1, '2022-05-19 21:24:31', '2022-08-13 07:24:40', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (23, '/frequencyTest/serverTimeNeedLogin', 19, 'serverTimeNeedLogin', NULL, '', 3600, 100, 0, 0, 'none', 'frequency2', '', 1, '', '', 0, 1, '2022-05-19 21:24:31', '2022-08-13 07:24:41', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (47, '/frequencyLogin/login', 26, 'frequencyLogin', NULL, '', 5, 4, 0, 0, 'none', 'frequency', 'username', 0, '', '', 0, 1, '2022-05-20 06:39:21', '2022-08-13 07:42:55', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (48, '/frequencyLogin/login', 26, 'frequencyLogin', NULL, '', 3600, 30, 0, 0, 'none', 'frequency2', 'username', 0, '', '', 0, 1, '2022-05-20 06:39:21', '2022-08-13 07:42:56', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (142, '/error', 27, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-21 08:08:51', '2022-05-21 08:08:51', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (166, '/frequencyTest/serverTimeUri', 16, NULL, NULL, NULL, 5, 5, NULL, NULL, NULL, NULL, NULL, 0, '超限提示test', '超限提示test2', 1, 1, '2022-05-21 08:35:47', '2022-05-21 08:47:38', NULL, 2, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (167, '/dflcore/dflFrequency/list2', 28, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-21 08:35:56', '2022-05-21 08:35:56', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (168, '/dflcore/dflFrequency/save', 35, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-21 08:36:09', '2022-05-21 08:36:09', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (172, '/dflcore/dflBlackWhite/list2', 32, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-21 08:46:07', '2022-05-21 08:46:07', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (173, '/dflcore/dflBlackWhiteItem/list2', 31, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-21 08:46:07', '2022-05-21 08:46:07', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (174, '/dflcore/dflLocks/list2', 33, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-21 08:46:13', '2022-05-21 08:46:13', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (175, '/dflLogs/dflAuditLog/list2', 93, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-21 08:46:33', '2022-05-21 08:46:33', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (176, '/dflcore/dflFrequency/delete', 81, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0, 1, '2022-05-21 08:47:38', '2022-05-21 08:47:38', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (178, '/frequencyLogin/rsaKey', 25, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-21 15:53:16', '2022-05-21 15:53:16', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (179, '/dflSystem/dflUser/list2', 45, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-21 15:56:18', '2022-05-21 15:56:18', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (180, '/frequencyTest/serverTimeUri', 16, NULL, NULL, NULL, 5, 5, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-21 15:58:26', '2022-05-21 17:59:38', NULL, -1, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (181, '/frequencyTest/waitLockTestUser', 17, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-21 16:01:59', '2022-05-21 16:01:59', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (182, '/dflLogs/dflOutLimitLog/list2', 105, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-21 16:02:34', '2022-05-21 16:02:34', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (183, '/dflcore/dflBlackWhite/save', 43, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-21 22:40:12', '2022-05-21 22:40:12', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (184, '/dflcore/dflLocks/save', 36, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-21 23:24:58', '2022-05-21 23:24:58', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (185, '/dflLogs/dflOutLockLog/list2', 107, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-21 23:29:22', '2022-05-21 23:29:22', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (186, '/dflLogs/dflOutLimitLog/listCount', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-22 00:05:50', '2022-05-22 00:05:50', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (187, '/frequency/getRunDays', 37, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-22 07:44:45', '2022-05-22 07:44:45', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (188, '/frequency/getRunCountTypeByDay', 38, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-22 07:44:45', '2022-05-22 07:44:45', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (189, '/frequency/requestScans', 39, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-22 07:44:45', '2022-05-22 07:44:45', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (190, '/dflBasedata/dflType/list2', 29, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-22 07:45:00', '2022-05-22 07:45:00', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (191, '/dflBasedata/dflTypeItem/list2', 30, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-22 07:45:00', '2022-05-22 07:45:00', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (192, '/frequency/requests', 53, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 00:21:14', '2022-05-25 00:21:14', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (193, '/dflSystem/dflSystemConfig/findSysconfigByParentId', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 00:21:27', '2022-05-25 00:21:27', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (194, '/frequencyReset/resetTicket', 162, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 04:02:13', '2022-05-25 04:02:13', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (195, '/dflSystem/dflUser/save', 146, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:28:12', '2022-05-25 07:28:12', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (196, '/dflLogs/dflRequestScans/list2', 48, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:28:26', '2022-05-25 07:28:26', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (197, '/frequencyTest/waitLockTestOrderZk', 23, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:28:53', '2022-05-25 07:28:53', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (198, '/frequencyTest/waitLockTestOrderEtcdKv', 21, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:29:23', '2022-05-25 07:29:23', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (199, '/frequency/limits', 55, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:29:43', '2022-05-25 07:29:43', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (200, '/frequency/locks', 50, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:36:00', '2022-05-25 07:36:00', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (201, '/dflSystem/dflSystemConfig/view', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:38:18', '2022-05-25 07:38:18', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (202, '/dflSystem/dflSystemConfig/save', 139, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:39:01', '2022-05-25 07:39:01', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (203, '/dflSystem/dflRole/list2', 127, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:39:38', '2022-05-25 07:39:38', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (204, '/dflSystem/dflRole/name/list', 126, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:39:41', '2022-05-25 07:39:41', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (205, '/dflSystem/dflUser/name/list', 145, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:39:41', '2022-05-25 07:39:41', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (206, '/dflSystem/dflUserRole/list2', 154, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:39:41', '2022-05-25 07:39:41', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (207, '/dflSystem/dflUserRole/save', 151, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:40:40', '2022-05-25 07:40:40', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (208, '/dflSystem/dflUser/changePassword', 142, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:41:12', '2022-05-25 07:41:12', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (209, '/dflcore/dflBlackWhiteItem/save', 44, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:42:18', '2022-05-25 07:42:18', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (210, '/dflSystem/dflResource/list2', 118, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 07:42:33', '2022-05-25 07:42:33', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (211, '/frequencyTest/waitLockTestOrderEtcdLock', 22, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-25 23:05:09', '2022-05-25 23:05:09', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (212, '/frequencyReset/imageCaptcha', 163, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-28 19:21:07', '2022-05-28 19:21:07', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (213, '/frequencyTest/monitor', 34, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-05-28 22:34:27', '2022-05-28 22:34:27', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (214, '/dflSystem/dflSystemConfig/delete', 138, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-06-03 19:16:45', '2022-06-03 19:16:45', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (215, '/frequencyTest/serverTimeFreqDevice', 15, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-06-09 22:22:41', '2022-06-09 22:22:41', NULL, NULL, 'GET', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (216, '/frequencyTest/serverTimeFreq120', 166, 'serverTimeFreq120', NULL, '', 120, 5, 0, 0, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-06-09 22:22:58', '2022-06-09 22:22:58', NULL, NULL, NULL, NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (217, '/frequencyTest/serverTimeFreq120', 166, 'serverTimeFreq120', NULL, '', 3600, 100, 0, 0, 'none', 'frequency2', '', 0, '', '', 0, 1, '2022-06-09 22:22:58', '2022-06-09 22:22:58', NULL, NULL, NULL, NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (218, '/frequencyTest/serverTimeFreq', 14, 'serverTimeFreqRes', NULL, '', 300, 10, 14, 14, 'none', 'frequency3', 'dataId', 0, '', '', 0, 1, '2022-06-10 22:47:50', '2022-06-10 22:47:50', NULL, NULL, NULL, NULL, 1, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (219, '/frequencyTest/serverTimeFreq', 14, 'serverTimeFreqIp', NULL, '', 300, 10, 14, 20, 'none', 'frequency3', 'dataId', 0, '', '', 0, 1, '2022-06-10 22:47:50', '2022-06-10 22:47:50', NULL, NULL, NULL, NULL, 1, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (220, '/frequencyTest/serverTimeFreq', 14, 'serverTimeFreqRes', NULL, '', 360, 10, 0, 0, 'none', 'frequency3', 'dataId', 0, '', '', 0, 1, '2022-06-14 21:16:52', '2022-06-14 21:16:52', NULL, NULL, NULL, NULL, 1, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (221, '/frequencyTest/serverTimeFreq', 14, 'serverTimeFreqIp', NULL, '', 360, 10, 0, 10, 'none', 'frequency3', 'dataId', 0, '', '', 0, 1, '2022-06-14 21:16:52', '2022-06-14 21:16:52', NULL, NULL, NULL, NULL, 1, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (222, '/frequencyTest/serverTimeFreqIpUser', 165, 'serverTimeFreqIpUser', NULL, '', 5, 5, 0, 7, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-06-17 22:59:42', '2022-08-13 07:24:02', NULL, NULL, NULL, NULL, 0, 4, 0);
INSERT INTO `dfl_frequency` VALUES (223, '/frequencyTest/serverTimeFreqUserIp', 170, 'serverTimeFreqUserIp', NULL, '', 5, 100, 7, 0, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-06-17 22:59:45', '2022-08-13 07:24:15', NULL, NULL, NULL, NULL, 0, 3, 0);
INSERT INTO `dfl_frequency` VALUES (224, '/frequencyTest/serverTimeJsonFreq', 168, 'serverTimeJsonFreq', NULL, '', 5, 5, 0, 0, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-06-17 22:59:51', '2022-08-13 07:24:22', NULL, NULL, NULL, NULL, 0, 1, 0);
INSERT INTO `dfl_frequency` VALUES (225, '/frequencyTest/serverTimeStreamFreq', 167, 'serverTimeStreamFreq', NULL, '', 5, 5, 0, 0, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-06-17 23:00:15', '2022-08-13 07:24:49', NULL, NULL, NULL, NULL, 0, 1, 0);
INSERT INTO `dfl_frequency` VALUES (226, '/frequencyTest/serverTimeFreqLimitIp', NULL, 'serverTimeFreqLimitIp', NULL, '', 5, 10, 0, 0, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-06-18 07:35:52', '2022-08-13 07:24:09', NULL, NULL, NULL, NULL, 0, 2, 0);
INSERT INTO `dfl_frequency` VALUES (227, '/frequencyTest/serverTimeFreq', 14, 'serverTimeFreqRes', NULL, '', 60, 10, 0, 0, 'none', 'frequency3', 'dataId', 0, '', '', 0, 1, '2022-06-18 07:39:22', '2022-06-18 07:39:22', NULL, NULL, NULL, NULL, 1, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (228, '/frequencyTest/serverTimeFreq', 14, 'serverTimeFreqIp', NULL, '', 60, 10, 0, 10, 'none', 'frequency3', 'dataId', 0, '', '', 0, 1, '2022-06-18 07:39:22', '2022-06-18 07:39:22', NULL, NULL, NULL, NULL, 1, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (229, '/frequencyTest/serverTimeFreqIp', 18, 'serverTimeFreqIp', NULL, '', 30, 100, 7, 7, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-06-18 22:32:22', '2022-06-18 22:32:22', NULL, NULL, NULL, NULL, 0, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (230, '/frequencyReset/resetLimits', 161, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-07-01 19:05:01', '2022-07-01 19:05:01', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (231, '/frequencyTest/serverTimeFreqResIp', NULL, 'serverTimeFreqResIp', NULL, '', 60, 10, NULL, NULL, 'none', 'frequency2', 'dataId', 0, '', '', 0, 1, '2022-08-13 07:24:01', '2022-08-13 07:24:01', NULL, NULL, NULL, NULL, NULL, 5, 0);
INSERT INTO `dfl_frequency` VALUES (232, '/frequencyTest/serverTimeFreqResUser', NULL, 'serverTimeFreqResUser', NULL, '', 60, 10, NULL, NULL, 'none', 'frequency2', 'dataId', 0, '', '', 0, 1, '2022-08-13 07:24:39', '2022-08-13 07:24:39', NULL, NULL, NULL, NULL, NULL, 6, 0);
INSERT INTO `dfl_frequency` VALUES (233, '/frequencyTest/serverTimeFreqAttrCheck', 181, 'serverTimeFreqAttrCheck', NULL, '', 5, 50, NULL, NULL, 'none', 'frequency', 'dataId', 0, '', '', 0, 1, '2022-08-13 07:24:42', '2022-08-13 07:24:42', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (234, '/frequencyTest/serverTimeFreqAttrCheck', 181, 'serverTimeFreqAttrCheck', NULL, '', 3600, 100, NULL, NULL, 'none', 'frequency2', 'dataId', 0, '', '', 0, 1, '2022-08-13 07:24:43', '2022-08-13 07:24:43', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (235, '/frequencyTest/serverTimeFreqAttr', NULL, 'serverTimeFreqAttr', NULL, '', 5, 50, NULL, NULL, 'none', 'frequency', 'dataId', 0, '', '', 0, 1, '2022-08-13 07:24:48', '2022-08-13 07:24:48', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (236, '/frequencyTest/serverTimeFreqIp', 18, 'serverTimeFreqIpUser', NULL, '', 30, 10, NULL, NULL, 'none', 'frequency', '', 0, '', '', 0, 1, '2022-08-13 07:25:02', '2022-08-13 07:25:02', NULL, NULL, NULL, NULL, NULL, 4, 0);
INSERT INTO `dfl_frequency` VALUES (237, '/frequencyTest/serverTimeFreqIp', 18, 'serverTimeFreqUserIp', NULL, '', 30, 10, NULL, NULL, 'none', 'frequency2', '', 0, '', '', 0, 1, '2022-08-13 07:25:04', '2022-08-13 07:25:04', NULL, NULL, NULL, NULL, NULL, 3, 0);
INSERT INTO `dfl_frequency` VALUES (238, '/frequencyTest/serverTimeFreqRes', 182, 'serverTimeFreqResIpCount', NULL, '', 60, 10, NULL, NULL, 'none', 'frequency2', 'dataId', 0, '', '', 0, 1, '2022-08-13 07:25:28', '2022-08-13 07:25:28', NULL, NULL, NULL, NULL, NULL, 5, 0);
INSERT INTO `dfl_frequency` VALUES (239, '/frequencyTest/serverTimeFreqRes', 182, 'serverTimeFreqResUserCount', NULL, '', 60, 10, NULL, NULL, 'none', 'frequency3', 'dataId', 0, '', '', 0, 1, '2022-08-13 07:25:29', '2022-08-13 07:25:29', NULL, NULL, NULL, NULL, NULL, 6, 0);
INSERT INTO `dfl_frequency` VALUES (240, '/frequencyTest/serverTimeFreqAttr', 177, 'serverTimeFreqAttr', NULL, '', 3600, 100, NULL, NULL, 'none', 'frequency2', 'dataId', 0, '', '', 0, 1, '2022-08-13 07:25:30', '2022-08-13 07:25:30', NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `dfl_frequency` VALUES (241, '/dflBasedata/dflType/save', 40, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-08-13 07:45:57', '2022-08-13 07:45:57', NULL, NULL, 'POST', NULL, NULL, NULL, 0);
INSERT INTO `dfl_frequency` VALUES (242, '/dflBasedata/dflTypeItem/save', 41, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uriConfig', NULL, 0, NULL, NULL, 0, 1, '2022-08-13 07:46:35', '2022-08-13 07:46:35', NULL, NULL, 'POST', NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for dfl_locks
-- ----------------------------
DROP TABLE IF EXISTS `dfl_locks`;
CREATE TABLE `dfl_locks`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uri_id` int NULL DEFAULT NULL,
  `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `time` int NULL DEFAULT NULL,
  `lock_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'redis,etcdKv,etcdLock,zk',
  `attr_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `err_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `status` tinyint NOT NULL COMMENT '状态:是否有效0无效，1有效',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '分布式锁配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_locks
-- ----------------------------
INSERT INTO `dfl_locks` VALUES (3, '/frequencyTest/waitLockTestUser', 17, 'waitLockTestUser', NULL, 5, 'redis', '', '任务%s正在执行1', 0, 1, '2022-05-21 06:05:00', '2022-05-21 23:24:58', NULL, -1, NULL);
INSERT INTO `dfl_locks` VALUES (4, '/frequencyTest/waitLockTestOrder', 20, 'waitLockTestOrder', NULL, 2, 'redis', 'orderId', '任务%s正在执行23', 0, 1, '2022-05-21 06:05:03', '2022-05-21 23:28:47', NULL, -1, NULL);
INSERT INTO `dfl_locks` VALUES (5, '/frequencyTest/waitLockTestOrderZk', 23, 'waitLockTestOrderZk', NULL, 5, 'zk', 'orderId', '任务%s正在执行', 0, 1, '2022-05-25 07:28:53', '2022-05-25 07:28:53', NULL, NULL, NULL);
INSERT INTO `dfl_locks` VALUES (6, '/frequencyTest/waitLockTestOrderEtcdKv', 21, 'waitLockTestOrderEtcdKv', NULL, 5, 'etcdKv', 'orderId', '任务%s正在执行', 0, 1, '2022-05-25 07:29:22', '2022-05-25 07:29:22', NULL, NULL, NULL);
INSERT INTO `dfl_locks` VALUES (7, '/frequencyTest/waitLockTestOrderEtcdLock', 22, 'waitLockTestOrderEtcdLock', NULL, 5, 'etcdLock', 'orderId', '任务%s正在执行', 0, 1, '2022-05-25 23:05:09', '2022-05-25 23:05:09', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for dfl_log_user
-- ----------------------------
DROP TABLE IF EXISTS `dfl_log_user`;
CREATE TABLE `dfl_log_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'userId',
  `user_type` tinyint NULL DEFAULT NULL COMMENT '用户类型',
  `nickname` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'nickname',
  `sys_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `register_ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `if_del` tinyint NULL DEFAULT NULL COMMENT 'if_del',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'create_time',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'remark',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用于非数字的userId转Long型uid，以减少日志存储量，并提高性能\r\n' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for dfl_out_limit_log
-- ----------------------------
DROP TABLE IF EXISTS `dfl_out_limit_log`;
CREATE TABLE `dfl_out_limit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uri_id` int NULL DEFAULT NULL,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户端用户id',
  `uid` bigint NULL DEFAULT NULL,
  `lang` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `limit_count` int NULL DEFAULT NULL COMMENT '限制次数',
  `time_second` int NULL DEFAULT NULL COMMENT '间隔时间',
  `attr_value` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sub_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `frequency_id` int NULL DEFAULT NULL,
  `origin` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `out_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `out_limit_type` tinyint NULL DEFAULT NULL COMMENT '1 frequency, 2 white, 3black',
  `limit_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sys_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'i ios,a android h h5',
  `ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户IP地址',
  `device_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备号',
  `req_count` int NULL DEFAULT NULL COMMENT '请求次数',
  `err_msg` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `user_type` tinyint NULL DEFAULT NULL COMMENT '0未登入，1用户Id，2用户账号',
  `run_time` int NULL DEFAULT NULL,
  `if_resource` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uri`(`uri_id`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE,
  INDEX `idx_uid`(`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2155 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '频率限制超限日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for dfl_out_lock_log
-- ----------------------------
DROP TABLE IF EXISTS `dfl_out_lock_log`;
CREATE TABLE `dfl_out_lock_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uri_id` int NULL DEFAULT NULL,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uid` bigint NULL DEFAULT NULL,
  `attr_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `lock_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '锁类型:redis,etcdKv,etcdLock,zk',
  `time_second` int NULL DEFAULT NULL,
  `sys_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `device_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE,
  INDEX `idx_uri`(`uri_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 467 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '分布式锁超限日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for dfl_request_scans
-- ----------------------------
DROP TABLE IF EXISTS `dfl_request_scans`;
CREATE TABLE `dfl_request_scans`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口uri',
  `method_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法名',
  `annotations` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法所有注解',
  `bean_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类名',
  `method` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'GET/POST/PUT等',
  `if_del` tinyint NULL DEFAULT NULL COMMENT '是否删除',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态:是否有效0无效，1有效',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pkg` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '包名',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uri`(`uri`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 178 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '所有接口方法（通过扫码所有controller接口）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_request_scans
-- ----------------------------
INSERT INTO `dfl_request_scans` VALUES (14, '/frequencyTest/serverTimeFreq', 'serverTimeFreq', 'Frequency,Frequency2,GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-11 23:20:34', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (15, '/frequencyTest/serverTimeFreqDevice', 'serverTimeFreqDevice', 'GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-11 23:21:01', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (16, '/frequencyTest/serverTimeUri', 'serverTimeUri', 'GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-11 23:21:11', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (17, '/frequencyTest/waitLockTestUser', 'waitLockTestUser', 'GetMapping,RequestLock', 'FrequencyTestController', 'GET', 0, 1, '2022-05-11 23:21:33', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (18, '/frequencyTest/serverTimeFreqIp', 'serverTimeFreqIp', 'Frequency,GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-11 23:54:44', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (19, '/frequencyTest/serverTimeNeedLogin', 'serverTimeNeedLogin', 'Frequency,Frequency2,GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-12 08:46:35', '2022-05-21 07:28:20', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (20, '/frequencyTest/waitLockTestOrder', 'waitLockTestOrder', 'GetMapping,RequestLock', 'FrequencyTestController', 'GET', 0, 1, '2022-05-12 08:59:54', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (21, '/frequencyTest/waitLockTestOrderEtcdKv', 'waitLockTestOrderEtcdKv', 'GetMapping,RequestLock', 'FrequencyTestController', 'GET', 0, 1, '2022-05-14 06:19:29', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (22, '/frequencyTest/waitLockTestOrderEtcdLock', 'waitLockTestOrderEtcdLock', 'GetMapping,RequestLock', 'FrequencyTestController', 'GET', 0, 1, '2022-05-14 06:28:28', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (23, '/frequencyTest/waitLockTestOrderZk', 'waitLockTestOrderZk', 'GetMapping,RequestLock', 'FrequencyTestController', 'GET', 0, 1, '2022-05-14 06:30:14', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (24, '/frequencyDemo/serverTimeFreq', 'serverTimeFreq', 'Frequency,GetMapping', 'FrequencyDemoController', 'GET', 0, 1, '2022-05-16 07:20:47', '2022-05-21 07:28:14', NULL, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (25, '/frequencyLogin/rsaKey', 'getRsaKey', 'GetMapping', 'FrequencyLoginController', 'GET', 0, 1, '2022-05-19 21:16:03', '2022-05-21 07:28:14', NULL, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (26, '/frequencyLogin/login', 'getlogin', 'GetMapping', 'FrequencyLoginController', 'GET', 0, 1, '2022-05-19 21:16:04', '2022-05-21 07:28:14', NULL, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (27, '/error', NULL, NULL, NULL, NULL, 0, 1, '2022-05-19 21:16:06', '2022-05-19 21:16:06', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `dfl_request_scans` VALUES (28, '/dflcore/dflFrequency/list2', 'queryPage2', 'RequestMapping', 'DflFrequencyController', 'POST,GET,', 0, 1, '2022-05-19 21:16:45', '2022-05-21 07:28:16', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (29, '/dflBasedata/dflType/list2', 'findByPage', 'RequestMapping', 'DflTypeController', 'POST,GET,', 0, 1, '2022-05-19 21:21:06', '2022-05-21 07:28:15', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (30, '/dflBasedata/dflTypeItem/list2', 'findByPage', 'RequestMapping', 'DflTypeItemController', 'POST,GET,', 0, 1, '2022-05-19 21:21:07', '2022-05-21 07:28:15', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (31, '/dflcore/dflBlackWhiteItem/list2', 'queryPage2', 'RequestMapping', 'DflBlackWhiteItemController', 'POST,GET,', 0, 1, '2022-05-19 21:21:13', '2022-05-21 07:28:16', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (32, '/dflcore/dflBlackWhite/list2', 'queryPage2', 'RequestMapping', 'DflBlackWhiteController', 'POST,GET,', 0, 1, '2022-05-19 21:21:14', '2022-05-21 07:28:15', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (33, '/dflcore/dflLocks/list2', 'queryPage2', 'RequestMapping', 'DflLocksController', 'POST,GET,', 0, 1, '2022-05-19 21:21:17', '2022-05-21 07:28:16', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (34, '/frequencyTest/monitor', 'monitor', 'GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-19 22:40:44', '2022-05-21 07:28:21', NULL, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (35, '/dflcore/dflFrequency/save', 'edit', 'RequestMapping', 'DflFrequencyController', 'POST,GET,', 0, 1, '2022-05-19 22:47:12', '2022-05-21 07:28:16', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (36, '/dflcore/dflLocks/save', 'edit', 'RequestMapping', 'DflLocksController', 'POST,GET,', 0, 1, '2022-05-19 23:35:32', '2022-05-21 07:28:16', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (37, '/frequency/getRunDays', 'getRunDays', 'RequestMapping', 'FrequencyController', 'GET,POST,', 0, 1, '2022-05-19 23:42:11', '2022-05-21 07:28:14', NULL, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (38, '/frequency/getRunCountTypeByDay', 'getRunCountTypeByDay', 'RequestMapping', 'FrequencyController', 'GET,POST,', 0, 1, '2022-05-19 23:42:12', '2022-05-21 07:28:14', NULL, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (39, '/frequency/requestScans', 'requestScans', 'RequestMapping', 'FrequencyController', 'POST,GET,', 0, 1, '2022-05-19 23:42:12', '2022-05-21 07:28:14', NULL, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (40, '/dflBasedata/dflType/save', 'edit', 'RequestMapping', 'DflTypeController', 'POST,GET,', 0, 1, '2022-05-20 06:40:59', '2022-05-21 07:28:15', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (41, '/dflBasedata/dflTypeItem/save', 'edit', 'RequestMapping', 'DflTypeItemController', 'POST,GET,', 0, 1, '2022-05-20 06:41:14', '2022-05-21 07:28:15', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (42, '/dflBasedata/dflTypeItem/typeItems', 'getTypeItems', 'RequestMapping', 'DflTypeItemController', 'REQUEST', 0, 1, '2022-05-20 06:43:35', '2022-05-21 07:28:15', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (43, '/dflcore/dflBlackWhite/save', 'edit', 'RequestMapping', 'DflBlackWhiteController', 'POST,GET,', 0, 1, '2022-05-20 06:48:56', '2022-05-21 07:28:15', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (44, '/dflcore/dflBlackWhiteItem/save', 'edit', 'RequestMapping', 'DflBlackWhiteItemController', 'POST,GET,', 0, 1, '2022-05-20 06:49:06', '2022-05-21 07:28:16', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (45, '/dflSystem/dflUser/list2', 'findByPage', 'RequestMapping', 'DflUserController', 'POST,GET,', 0, 1, '2022-05-21 00:30:51', '2022-05-21 07:28:20', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (46, '/dflBasedata/dflTypeItem/delete', 'delete', 'RequestMapping', 'DflTypeItemController', 'POST,DELETE,', 0, 1, '2022-05-21 06:19:17', '2022-05-21 07:28:15', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (47, '/dflLogs/dflRequestScans/list', 'queryPage', 'RequestMapping', 'DflRequestScansController', 'POST,GET,', 0, 1, '2022-05-21 06:33:43', '2022-05-21 07:28:18', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (48, '/dflLogs/dflRequestScans/list2', 'queryPage2', 'RequestMapping', 'DflRequestScansController', 'POST,GET,', 0, 1, '2022-05-21 06:34:16', '2022-05-21 07:28:18', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (49, '/dflLogs/dflRequestScans/updateScanController', 'updateScanController', 'RequestMapping', 'DflRequestScansController', 'POST,GET,', 0, 1, '2022-05-21 07:11:21', '2022-05-21 07:28:18', NULL, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (50, '/frequency/locks', 'locksClear', 'CheckAuthorization,DeleteMapping', 'FrequencyController', 'DELETE', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:13', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (51, '/frequency/evictTimes', 'evictTimes', 'CheckAuthorization,RequestMapping', 'FrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (52, '/frequency/requestMaxRunTimes', 'requestMaxRunTimes', 'RequestMapping', 'FrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (53, '/frequency/requests', 'requests', 'RequestMapping', 'FrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (54, '/frequency/ipConvert', 'ipConvert', 'GetMapping', 'FrequencyController', 'GET', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (55, '/frequency/limits', 'limitsClear', 'CheckAuthorization,DeleteMapping', 'FrequencyController', 'DELETE', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (56, '/frequency/evict', 'evict', 'CheckAuthorization,RequestMapping', 'FrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (57, '/frequency/evictIpUser', 'evictIpUser', 'CheckAuthorization,RequestMapping', 'FrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (58, '/frequency/lockEvict', 'lockEvict', 'CheckAuthorization,RequestMapping', 'FrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:22', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (59, '/frequency/evictUserIp', 'evictUserIp', 'CheckAuthorization,RequestMapping', 'FrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (60, '/frequencyDemo/serverTime', 'serverTime', 'GetMapping', 'FrequencyDemoController', 'GET', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (61, '/frequencyDemo/waitTimeTest', 'waitTimeTest', 'GetMapping,RequestLock', 'FrequencyDemoController', 'GET', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (62, '/frequencyLogin/user', 'getUser', 'GetMapping', 'FrequencyLoginController', 'GET', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (63, '/frequencyLogin/index', 'index', 'GetMapping', 'FrequencyLoginController', 'GET', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (64, '/dflBasedata/dflType/index', 'index', 'RequestMapping', 'DflTypeController', 'GET,', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:14', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (65, '/dflBasedata/dflType/delete', 'delete', 'RequestMapping', 'DflTypeController', 'POST,DELETE,', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (66, '/dflBasedata/dflType/list', 'queryPage', 'RequestMapping', 'DflTypeController', 'POST,GET,', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (67, '/dflBasedata/dflType/update', 'update', 'RequestMapping', 'DflTypeController', 'POST,GET,', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (68, '/dflBasedata/dflTypeItem/index', 'index', 'RequestMapping', 'DflTypeItemController', 'GET,', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (69, '/dflBasedata/dflTypeItem/typeItem', 'getTypeItem', 'RequestMapping', 'DflTypeItemController', 'REQUEST', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (70, '/dflBasedata/dflTypeItem/update', 'update', 'RequestMapping', 'DflTypeItemController', 'POST,GET,', 0, 1, '2022-05-21 07:11:23', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (71, '/dflBasedata/dflTypeItem/list', 'queryPage', 'RequestMapping', 'DflTypeItemController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflbasedata.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (72, '/dflcore/dflBlackWhite/index', 'index', 'RequestMapping', 'DflBlackWhiteController', 'GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (73, '/dflcore/dflBlackWhite/list', 'queryPage', 'RequestMapping', 'DflBlackWhiteController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (74, '/dflcore/dflBlackWhite/delete', 'delete', 'RequestMapping', 'DflBlackWhiteController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (75, '/dflcore/dflBlackWhite/update', 'update', 'RequestMapping', 'DflBlackWhiteController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (76, '/dflcore/dflBlackWhiteItem/index', 'index', 'RequestMapping', 'DflBlackWhiteItemController', 'GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:15', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (77, '/dflcore/dflBlackWhiteItem/delete', 'delete', 'RequestMapping', 'DflBlackWhiteItemController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (78, '/dflcore/dflBlackWhiteItem/update', 'update', 'RequestMapping', 'DflBlackWhiteItemController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (79, '/dflcore/dflBlackWhiteItem/list', 'queryPage', 'RequestMapping', 'DflBlackWhiteItemController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (80, '/dflcore/dflFrequency/index', 'index', 'RequestMapping', 'DflFrequencyController', 'GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (81, '/dflcore/dflFrequency/delete', 'delete', 'RequestMapping', 'DflFrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (82, '/dflcore/dflFrequency/list', 'queryPage', 'RequestMapping', 'DflFrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (83, '/dflcore/dflFrequency/update', 'update', 'RequestMapping', 'DflFrequencyController', 'POST,GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (84, '/dflcore/dflLocks/index', 'index', 'RequestMapping', 'DflLocksController', 'GET,', 0, 1, '2022-05-21 07:11:24', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (85, '/dflcore/dflLocks/delete', 'delete', 'RequestMapping', 'DflLocksController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (86, '/dflcore/dflLocks/list', 'queryPage', 'RequestMapping', 'DflLocksController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (87, '/dflcore/dflLocks/update', 'update', 'RequestMapping', 'DflLocksController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflcore.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (88, '/dflLogs/dflAuditLog/index', 'index', 'GetMapping', 'DflAuditLogController', 'GET', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (89, '/dflLogs/dflAuditLog/list', 'queryPage', 'CheckAuthorization,RequestMapping', 'DflAuditLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (90, '/dflLogs/dflAuditLog/update', 'update', 'RequestMapping', 'DflAuditLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:16', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (91, '/dflLogs/dflAuditLog/save', 'edit', 'RequestMapping', 'DflAuditLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (92, '/dflLogs/dflAuditLog/delete', 'delete', 'RequestMapping', 'DflAuditLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (93, '/dflLogs/dflAuditLog/list2', 'queryPage2', 'CheckAuthorization,RequestMapping', 'DflAuditLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (94, '/dflLogUser/index', 'index', 'RequestMapping', 'DflLogUserController', 'GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (95, '/dflLogUser/save', 'edit', 'RequestMapping', 'DflLogUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (96, '/dflLogUser/list', 'queryPage', 'RequestMapping', 'DflLogUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (97, '/dflLogUser/update', 'update', 'RequestMapping', 'DflLogUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (98, '/dflLogUser/list2', 'queryPage2', 'RequestMapping', 'DflLogUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (99, '/dflLogUser/delete', 'delete', 'RequestMapping', 'DflLogUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (100, '/dflLogs/dflOutLimitLog/index', 'index', 'GetMapping', 'DflOutLimitLogController', 'GET', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (101, '/dflLogs/dflOutLimitLog/update', 'update', 'RequestMapping', 'DflOutLimitLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (102, '/dflLogs/dflOutLimitLog/delete', 'delete', 'RequestMapping', 'DflOutLimitLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:25', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (103, '/dflLogs/dflOutLimitLog/list', 'queryPage', 'RequestMapping', 'DflOutLimitLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (104, '/dflLogs/dflOutLimitLog/save', 'edit', 'RequestMapping', 'DflOutLimitLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (105, '/dflLogs/dflOutLimitLog/list2', 'queryPage2', 'RequestMapping', 'DflOutLimitLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (106, '/dflLogs/dflOutLockLog/index', 'index', 'RequestMapping', 'DflOutLockLogController', 'GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (107, '/dflLogs/dflOutLockLog/list2', 'queryPage2', 'RequestMapping', 'DflOutLockLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (108, '/dflLogs/dflOutLockLog/list', 'queryPage', 'RequestMapping', 'DflOutLockLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (109, '/dflLogs/dflOutLockLog/delete', 'delete', 'RequestMapping', 'DflOutLockLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (110, '/dflLogs/dflOutLockLog/update', 'update', 'RequestMapping', 'DflOutLockLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:17', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (111, '/dflLogs/dflOutLockLog/save', 'edit', 'RequestMapping', 'DflOutLockLogController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (112, '/dflLogs/dflRequestScans/index', 'index', 'RequestMapping', 'DflRequestScansController', 'GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (113, '/dflLogs/dflRequestScans/delete', 'delete', 'RequestMapping', 'DflRequestScansController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (114, '/dflLogs/dflRequestScans/update', 'update', 'RequestMapping', 'DflRequestScansController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (115, '/dflLogs/dflRequestScans/save', 'edit', 'RequestMapping', 'DflRequestScansController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dfllogs.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (116, '/dflSystem/dflResource/index', 'index', 'RequestMapping', 'DflResourceController', 'GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (117, '/dflSystem/dflResource/delete', 'delete', 'RequestMapping', 'DflResourceController', 'POST,DELETE,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (118, '/dflSystem/dflResource/list2', 'queryPage2', 'RequestMapping', 'DflResourceController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (119, '/dflSystem/dflResource/list', 'queryPage', 'RequestMapping', 'DflResourceController', 'POST,GET,', 0, 1, '2022-05-21 07:11:26', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (120, '/dflSystem/dflResource/save', 'edit', 'RequestMapping', 'DflResourceController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (121, '/dflSystem/dflResource/update', 'update', 'RequestMapping', 'DflResourceController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (122, '/dflSystem/dflRole/update', 'update', 'CheckAuthorization,RequestMapping', 'DflRoleController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (123, '/dflSystem/dflRole/index', 'index', 'RequestMapping', 'DflRoleController', 'GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (124, '/dflSystem/dflRole/save', 'edit', 'CheckAuthorization,RequestMapping', 'DflRoleController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (125, '/dflSystem/dflRole/delete', 'delete', 'CheckAuthorization,RequestMapping', 'DflRoleController', 'POST,DELETE,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (126, '/dflSystem/dflRole/name/list', 'findNameByPage', 'RequestMapping,ResponseStatus', 'DflRoleController', 'REQUEST', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (127, '/dflSystem/dflRole/list2', 'findByPage', 'CheckAuthorization,RequestMapping', 'DflRoleController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (128, '/dflSystem/dflRole/list', 'queryPage', 'CheckAuthorization,RequestMapping', 'DflRoleController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:18', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (129, '/dflSystem/dflService/index', 'index', 'RequestMapping', 'DflServiceController', 'GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (130, '/dflSystem/dflService/list', 'queryPage', 'RequestMapping', 'DflServiceController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (131, '/dflSystem/dflService/list2', 'queryPage2', 'RequestMapping', 'DflServiceController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (132, '/dflSystem/dflService/delete', 'delete', 'RequestMapping', 'DflServiceController', 'POST,DELETE,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (133, '/dflSystem/dflService/update', 'update', 'RequestMapping', 'DflServiceController', 'POST,GET,', 0, 1, '2022-05-21 07:11:27', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (134, '/dflSystem/dflService/save', 'edit', 'RequestMapping', 'DflServiceController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (135, '/dflSystem/dflSystemConfig/index', 'index', 'RequestMapping', 'DflSystemConfigController', 'GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (136, '/dflSystem/dflSystemConfig/list2', 'findByPage', 'CheckAuthorization,RequestMapping', 'DflSystemConfigController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (137, '/dflSystem/dflSystemConfig/list', 'queryPage', 'CheckAuthorization,RequestMapping', 'DflSystemConfigController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (138, '/dflSystem/dflSystemConfig/delete', 'delete', 'CheckAuthorization,RequestMapping', 'DflSystemConfigController', 'POST,DELETE,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (139, '/dflSystem/dflSystemConfig/save', 'edit', 'CheckAuthorization,RequestMapping', 'DflSystemConfigController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (140, '/dflSystem/dflSystemConfig/update', 'update', 'CheckAuthorization,RequestMapping', 'DflSystemConfigController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (141, '/dflSystem/dflUser/index', 'index', 'RequestMapping', 'DflUserController', 'GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (142, '/dflSystem/dflUser/changePassword', 'changePassword', 'CheckAuthorization,RequestMapping', 'DflUserController', 'POST,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (143, '/dflSystem/dflUser/update', 'update', 'CheckAuthorization,RequestMapping', 'DflUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (144, '/dflSystem/dflUser/delete', 'delete', 'CheckAuthorization,RequestMapping', 'DflUserController', 'POST,DELETE,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (145, '/dflSystem/dflUser/name/list', 'findNameByPage', 'RequestMapping,ResponseStatus', 'DflUserController', 'REQUEST', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (146, '/dflSystem/dflUser/save', 'edit', 'CheckAuthorization,RequestMapping', 'DflUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:19', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (147, '/dflSystem/dflUser/list', 'queryPage', 'RequestMapping', 'DflUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (148, '/dflSystem/dflUser/findUserId', 'findUserId', 'RequestMapping', 'DflUserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (149, '/dflSystem/dflUserRole/index', 'index', 'RequestMapping', 'DflUserRoleController', 'GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (150, '/dflSystem/dflUserRole/delete', 'delete', 'CheckAuthorization,RequestMapping', 'DflUserRoleController', 'POST,DELETE,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (151, '/dflSystem/dflUserRole/save', 'edit', 'CheckAuthorization,RequestMapping', 'DflUserRoleController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (152, '/dflSystem/dflUserRole/list', 'queryPage', 'CheckAuthorization,RequestMapping', 'DflUserRoleController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (153, '/dflSystem/dflUserRole/update', 'update', 'CheckAuthorization,RequestMapping', 'DflUserRoleController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (154, '/dflSystem/dflUserRole/list2', 'findByPage', 'CheckAuthorization,RequestMapping', 'DflUserRoleController', 'POST,GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (155, '/user/index', 'index', 'RequestMapping', 'UserController', 'GET,', 0, 1, '2022-05-21 07:11:28', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (156, '/user/list', 'queryPage', 'RequestMapping', 'UserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (157, '/user/save', 'save', 'RequestMapping', 'UserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (158, '/user/findUserId', 'findUserId', 'RequestMapping', 'UserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (159, '/user/update', 'update', 'RequestMapping', 'UserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (160, '/user/delete', 'delete', 'RequestMapping', 'UserController', 'POST,GET,', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.mysql.dflsystem.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (161, '/frequencyReset/resetLimits', 'resetLimits', 'RequestMapping', 'FrequencyResetController', 'GET,POST,', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (162, '/frequencyReset/resetTicket', 'getResetTicket', 'GetMapping', 'FrequencyResetController', 'GET', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (163, '/frequencyReset/imageCaptcha', 'getImageCaptcha', 'RequestMapping', 'FrequencyResetController', 'GET,POST,', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (164, '/frequencyReset/captchaCode', 'getImageCaptchaCode', 'RequestMapping', 'FrequencyResetController', 'GET,POST,', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:20', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (165, '/frequencyTest/serverTimeFreqIpUser', 'serverTimeFreqIpUser', 'Frequency,GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:21', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (166, '/frequencyTest/serverTimeFreq120', 'serverTimeFreq120', 'Frequency,Frequency2,GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:21', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (167, '/frequencyTest/serverTimeStreamFreq', 'serverTimeStreamFreq', 'Frequency,PostMapping', 'FrequencyTestController', 'POST', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:21', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (168, '/frequencyTest/serverTimeJsonFreq', 'serverTimeJsonFreq', 'Frequency,PostMapping', 'FrequencyTestController', 'POST', 0, 1, '2022-05-21 07:11:29', '2022-05-21 07:28:21', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (169, '/frequencyTest/serverTime', 'serverTime', 'GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-21 07:11:30', '2022-05-21 07:28:21', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (170, '/frequencyTest/serverTimeFreqUserIp', 'serverTimeFreqUserIp', 'Frequency,GetMapping', 'FrequencyTestController', 'GET', 0, 1, '2022-05-21 07:11:30', '2022-05-21 07:28:21', -1, -1, NULL, 'org.ccs.opendfl.core.controller', NULL);
INSERT INTO `dfl_request_scans` VALUES (171, '/dflLogs/dflOutLimitLog/listCount', NULL, NULL, NULL, NULL, 0, 1, '2022-05-22 00:05:50', '2022-05-22 00:05:50', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `dfl_request_scans` VALUES (172, '/dflSystem/dflSystemConfig/findSysconfigByParentId', NULL, NULL, NULL, NULL, 0, 1, '2022-05-25 00:21:27', '2022-05-25 00:21:27', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `dfl_request_scans` VALUES (173, '/dflSystem/dflSystemConfig/view', NULL, NULL, NULL, NULL, 0, 1, '2022-05-25 07:38:18', '2022-05-25 07:38:18', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `dfl_request_scans` VALUES (174, '/frequencyTest/serverTimeFreqLimitIp', NULL, NULL, NULL, NULL, 0, 1, '2022-06-18 07:35:52', '2022-06-18 07:35:52', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `dfl_request_scans` VALUES (175, '/frequencyTest/serverTimeFreqResIp', NULL, NULL, NULL, NULL, 0, 1, '2022-08-13 07:24:01', '2022-08-13 07:24:01', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `dfl_request_scans` VALUES (176, '/frequencyTest/serverTimeFreqResUser', NULL, NULL, NULL, NULL, 0, 1, '2022-08-13 07:24:39', '2022-08-13 07:24:39', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `dfl_request_scans` VALUES (177, '/frequencyTest/serverTimeFreqAttr', NULL, NULL, NULL, NULL, 0, 1, '2022-08-13 07:24:48', '2022-08-13 07:24:48', NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for dfl_resource
-- ----------------------------
DROP TABLE IF EXISTS `dfl_resource`;
CREATE TABLE `dfl_resource`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `service_id` int NULL DEFAULT NULL COMMENT '所属服务ID',
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '接口uri',
  `uri_id` int NULL DEFAULT NULL,
  `method` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求类型(GET/POST/PUT)',
  `res_type` tinyint NULL DEFAULT NULL COMMENT '资源类型，0接口,1功能',
  `if_del` tinyint NULL DEFAULT NULL COMMENT '是否删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uri`(`uri`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE,
  INDEX `idx_uri_id`(`uri_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_resource
-- ----------------------------

-- ----------------------------
-- Table structure for dfl_role
-- ----------------------------
DROP TABLE IF EXISTS `dfl_role`;
CREATE TABLE `dfl_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `status` tinyint NOT NULL COMMENT '状态:是否有效0无效，1有效',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_role
-- ----------------------------
INSERT INTO `dfl_role` VALUES (1, 'admin', 'admin', NULL, 0, 1, '2022-05-04 08:52:08', NULL, NULL, NULL);
INSERT INTO `dfl_role` VALUES (2, 'manager', 'manager', NULL, 0, 1, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for dfl_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `dfl_role_resource`;
CREATE TABLE `dfl_role_resource`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL COMMENT '角色id',
  `resource_id` int NOT NULL COMMENT '资源id',
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_resource`(`resource_id`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_role_resource
-- ----------------------------

-- ----------------------------
-- Table structure for dfl_service
-- ----------------------------
DROP TABLE IF EXISTS `dfl_service`;
CREATE TABLE `dfl_service`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `service_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务类型',
  `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `descs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述及简介',
  `author` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作者及负责人',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务接口url',
  `wiki_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务文档url',
  `if_del` tinyint NULL DEFAULT NULL COMMENT '是否删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_service
-- ----------------------------

-- ----------------------------
-- Table structure for dfl_system_config
-- ----------------------------
DROP TABLE IF EXISTS `dfl_system_config`;
CREATE TABLE `dfl_system_config`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int NULL DEFAULT NULL COMMENT '父id',
  `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编码',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `value_type` tinyint NOT NULL COMMENT '参数值类型',
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数值',
  `value_json` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `value_default` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `status` tinyint NOT NULL COMMENT '状态：是否有效',
  `system_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统编码',
  `order_count` int NULL DEFAULT NULL COMMENT '排序号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE,
  INDEX `idx_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统参数配置(树形结构)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_system_config
-- ----------------------------
INSERT INTO `dfl_system_config` VALUES (1, 0, 'opendfl', 'opendfl', 1, NULL, NULL, NULL, NULL, 0, 1, NULL, 0, NULL, '2022-05-25 07:39:01', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (2, 1, 'lock', '分布锁配置', 1, NULL, NULL, NULL, NULL, 0, 1, NULL, 1, NULL, '2022-05-25 00:05:56', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (3, 1, 'frequency', '频率限制配置', 1, NULL, NULL, NULL, NULL, 0, 1, NULL, 2, NULL, '2022-05-25 00:06:04', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (4, 1, 'black', '黑名单', 1, NULL, NULL, NULL, NULL, 0, 1, NULL, 3, NULL, '2022-05-25 00:06:14', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (5, 1, 'white', '白名单', 1, NULL, NULL, NULL, NULL, 0, 1, NULL, 4, NULL, '2022-05-25 00:06:23', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (6, 1, 'baseLimit', 'baseLimit', 1, NULL, NULL, NULL, NULL, 0, 1, NULL, 4, NULL, '2022-05-25 00:06:23', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (16, 4, 'blacklist:ifDeviceRequire', '黑名单是否必须设备号', 1, '0', NULL, '1', '=1时，如果device_id为空也会拒绝', 0, 1, NULL, 0, '2022-05-25 00:05:08', '2022-06-09 20:31:07', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (17, 4, 'blacklist:ruleItems', '黑名单策略', 2, 'ip,user,device,', NULL, 'blackIp,blackUser,blackDeviceId,', '需要以\",\"结尾\nip,user,device,\n\n调整策略要生效，需要再去修改一下黑名单数据', 0, 1, NULL, 0, '2022-05-25 00:05:08', '2022-06-09 22:33:45', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (18, 5, 'whitelist:ifDeviceRequire', '白名单是否必须设备号', 1, '1', NULL, '1', 'code default', 0, 1, NULL, 0, '2022-05-25 00:05:09', '2022-05-25 00:05:09', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (19, 5, 'whitelist:ruleItems', '白名单策略', 2, 'ip,user,', NULL, 'whiteIp,whiteUser,', '需要以\",\"结尾\nip,user,origin,\n调整策略要生效，需要再去修改一下白名单数据', 0, 1, NULL, 0, '2022-06-01 07:19:49', '2022-06-09 22:33:35', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (20, 3, 'frequency:minRunTime', '统计运行时间最小值(ms)', 1, '500', NULL, '500', 'code default', 0, 1, NULL, 0, '2022-06-01 07:19:49', '2022-06-01 07:19:49', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (21, 3, 'limit:ruleItems', '频率限制策略', 2, 'limit,limitIp,userIp,ipUser,', NULL, 'limit,userCount,userIp,ipUser,', '需要以\",\"结尾\nlimit,limitIp,userIp,ipUser,', 0, 1, NULL, 0, '2022-06-01 07:19:49', '2022-06-18 07:36:30', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (23, 2, 'lock:ifActive', '分布式锁功能是否启用', 1, '1', NULL, '1', 'code default', 0, 1, NULL, 0, '2022-06-01 07:19:56', '2022-06-01 07:19:56', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (31, 3, 'limit:outLimitMinTime', '频率限制日志记录间格时间最小值', 1, '3600', NULL, '3600', 'code default', 0, 1, NULL, 0, '2022-06-02 07:15:48', '2022-06-02 07:15:48', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (32, 3, 'frequency:runTimeMonitor', '频率限制是否支持接口监控', 1, '1', NULL, '1', 'code default', 0, 1, NULL, 0, '2022-06-03 19:28:54', '2022-06-03 19:28:54', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (33, 3, 'frequency:redisPrefix', '频率限制redisKey前缀', 2, 'limitFreq', NULL, 'limitFreq', 'code default', 0, 1, NULL, 0, '2022-06-03 19:28:54', '2022-06-03 19:28:54', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (34, 3, 'frequency:initLogDebugCount', '启动时前面部份debug日志', 1, '100', NULL, '100', '如果前100行日志有输出，后面不再输出该日志', 0, 1, NULL, 0, '2022-06-03 19:28:54', '2022-06-03 23:30:12', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (35, 2, 'lock:redisPrefix', '分布式锁redisKey前缀', 2, 'limitLock', NULL, 'limitLock', 'code default', 0, 1, NULL, 0, '2022-06-03 19:29:25', '2022-06-03 19:29:25', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (36, 6, 'baseLimit:pageSizeMax', 'baseLimit最大每页行数', 1, '1000', NULL, '1000', 'code default', 0, 1, NULL, 0, '2022-06-09 22:21:11', '2022-06-09 22:22:21', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (37, 6, 'baseLimit:pageNumMax', 'baseLimit最大页数', 1, '1000', NULL, '1000', 'code default', 0, 1, NULL, 0, '2022-06-09 22:21:11', '2022-06-09 22:21:11', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (38, 6, 'baseLimit:totalRowMax', 'baseLimit最大总行数', 1, '10000', NULL, '10000', 'code default', 0, 1, NULL, 0, '2022-06-09 22:21:11', '2022-06-09 22:21:11', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (39, 6, 'baseLimit:pageNumMax', 'baseLimit最大页数', 1, '1000', NULL, '1000', 'code default', 0, 1, NULL, 0, '2022-06-09 22:21:11', '2022-06-09 22:21:11', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (40, 6, 'baseLimit:searchDateDayMax', 'baseLimit查询时段最大天数', 1, '90', NULL, '90', 'code default', 0, 1, NULL, 0, '2022-06-09 22:21:11', '2022-06-09 22:21:11', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (41, 6, 'baseLimit:totalRowMax', 'baseLimit最大总行数', 1, '10000', NULL, '10000', 'code default', 0, 1, NULL, 0, '2022-06-09 22:21:11', '2022-06-09 22:21:11', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (42, 5, 'blacklist:ifOriginRequire', '白名单是否必须origin', 1, '0', NULL, '0', '默认为0\n如果为1没有origin的接口将都不能调用，变成仅限有origin且origin正确的才能调用', 1, 1, NULL, 0, '2022-06-13 07:33:05', '2022-06-18 07:24:22', NULL, 2);
INSERT INTO `dfl_system_config` VALUES (43, 5, 'whitelist:ifOriginRequire', '白名单是否必须origin', 1, '0', NULL, '0', 'code default', 0, 1, NULL, 0, '2022-06-13 23:19:42', '2022-06-13 23:19:42', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (44, 3, 'frequency:limitIpRate', '频率限制数limitIp与limit的比率（limitIp=limit*{limitIpRate}）', 2, '2', NULL, '2', 'code default', 0, 1, NULL, 0, '2022-06-18 07:32:52', '2022-06-18 07:32:52', NULL, NULL);
INSERT INTO `dfl_system_config` VALUES (45, 6, 'baseLimit.log.exception.type', 'BaseException日志类型(full/simple)2分钟内生效', 2, 'simple', NULL, 'simple', 'code default', 0, 1, NULL, 0, '2022-08-13 07:24:04', '2022-08-13 07:24:04', NULL, NULL);

-- ----------------------------
-- Table structure for dfl_type
-- ----------------------------
DROP TABLE IF EXISTS `dfl_type`;
CREATE TABLE `dfl_type`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `status` tinyint NOT NULL COMMENT '状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_type
-- ----------------------------
INSERT INTO `dfl_type` VALUES (1, 'test', 'test', 'tt', 0, 1, NULL, '2022-05-08 20:17:07', NULL, 2);
INSERT INTO `dfl_type` VALUES (2, 'reqLockType', '分布式锁类型', 'test', 0, 1, '2022-05-08 20:13:29', '2022-05-21 06:08:08', -1, 2);
INSERT INTO `dfl_type` VALUES (3, 'status', '状态', NULL, 0, 1, '2022-05-08 23:38:34', '2022-05-08 23:38:34', -1, -1);
INSERT INTO `dfl_type` VALUES (4, 'ifDel', '是否删除', NULL, 0, 1, '2022-05-08 23:38:46', '2022-05-08 23:38:46', -1, -1);
INSERT INTO `dfl_type` VALUES (5, 'typeColor', '类型颜色', 't', 0, 1, '2022-05-13 23:05:29', '2022-05-13 23:05:29', 2, 2);
INSERT INTO `dfl_type` VALUES (6, 'reqSysType', '请求系统类型', NULL, 0, 1, '2022-05-15 09:28:37', '2022-05-15 09:28:37', 2, 2);
INSERT INTO `dfl_type` VALUES (7, 'blackWhiteLimitType', '黑白名单类型', NULL, 0, 1, '2022-05-20 06:40:59', '2022-05-20 06:43:02', 2, 2);
INSERT INTO `dfl_type` VALUES (8, 'blackWhtie策略', '黑白名单类型', NULL, 0, 1, '2022-05-20 06:42:10', '2022-05-20 06:57:10', 2, 2);
INSERT INTO `dfl_type` VALUES (9, 'freqLimitType', '频率限制类型', NULL, 0, 1, '2022-08-13 07:45:57', '2022-08-13 07:55:31', 2, 2);

-- ----------------------------
-- Table structure for dfl_type_item
-- ----------------------------
DROP TABLE IF EXISTS `dfl_type_item`;
CREATE TABLE `dfl_type_item`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_id` int NOT NULL COMMENT '类型id',
  `code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `color` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '值',
  `value2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '值2',
  `order_count` mediumint NULL DEFAULT NULL COMMENT '排序号',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `if_del` tinyint NOT NULL,
  `status` tinyint NOT NULL,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_type_item
-- ----------------------------
INSERT INTO `dfl_type_item` VALUES (1, 1, 'ttt', 'ttt', NULL, '1', '2', 1, 'test', 0, 1, NULL, '2022-05-08 20:39:04', NULL, 2);
INSERT INTO `dfl_type_item` VALUES (2, 1, 't1', 't1', NULL, 't', 't', 0, '1', 0, 1, '2022-05-08 20:38:04', '2022-05-08 20:38:04', 2, 2);
INSERT INTO `dfl_type_item` VALUES (3, 2, 'redis', 'redis快速失败模式', NULL, 't2', 't2', 0, 'Redis分布式锁，ifAbsent已存在时快速失败', 0, 1, '2022-05-08 20:38:19', '2022-05-21 06:10:58', 2, 2);
INSERT INTO `dfl_type_item` VALUES (4, 3, '0', '无效', 'red', NULL, NULL, 0, NULL, 0, 1, '2022-05-08 23:39:02', '2022-05-08 23:42:33', -1, -1);
INSERT INTO `dfl_type_item` VALUES (5, 3, '1', '有效', 'green', NULL, NULL, 1, NULL, 0, 1, '2022-05-08 23:39:10', '2022-05-08 23:42:38', -1, -1);
INSERT INTO `dfl_type_item` VALUES (6, 4, '0', '正常', 'green', NULL, NULL, 0, NULL, 0, 1, '2022-05-08 23:40:49', '2022-05-08 23:42:28', -1, -1);
INSERT INTO `dfl_type_item` VALUES (7, 4, '1', '已删除', 'red', NULL, NULL, 1, NULL, 0, 1, '2022-05-08 23:41:00', '2022-05-08 23:42:20', -1, -1);
INSERT INTO `dfl_type_item` VALUES (8, 5, 'red', '红色', 'red', NULL, NULL, 1, NULL, 0, 1, '2022-05-13 23:05:48', '2022-05-13 23:05:48', 2, 2);
INSERT INTO `dfl_type_item` VALUES (9, 5, 'black', '黑色', 'black', NULL, NULL, 9, NULL, 0, 1, '2022-05-13 23:06:04', '2022-05-13 23:07:09', 2, 2);
INSERT INTO `dfl_type_item` VALUES (10, 5, 'green', '绿色', 'green', NULL, NULL, 3, NULL, 0, 1, '2022-05-13 23:06:19', '2022-05-13 23:06:19', 2, 2);
INSERT INTO `dfl_type_item` VALUES (11, 5, 'blue', '蓝色', 'blue', NULL, NULL, 4, NULL, 0, 1, '2022-05-13 23:06:39', '2022-05-13 23:06:39', 2, 2);
INSERT INTO `dfl_type_item` VALUES (12, 5, 'yellow', '黄色', 'yellow', NULL, NULL, 2, NULL, 0, 1, '2022-05-13 23:07:00', '2022-05-13 23:07:22', 2, 2);
INSERT INTO `dfl_type_item` VALUES (13, 5, 'white', '白色', 'white', NULL, NULL, 8, NULL, 0, 1, '2022-05-13 23:07:39', '2022-05-13 23:07:39', 2, 2);
INSERT INTO `dfl_type_item` VALUES (14, 5, 'pink', '粉红色', 'pink', NULL, NULL, 7, NULL, 0, 1, '2022-05-13 23:08:27', '2022-05-13 23:08:27', 2, 2);
INSERT INTO `dfl_type_item` VALUES (15, 5, 'purple', '紫色', 'purple', NULL, NULL, 6, NULL, 0, 1, '2022-05-13 23:09:39', '2022-05-13 23:09:39', 2, 2);
INSERT INTO `dfl_type_item` VALUES (16, 5, 'cyan', '青色', 'cyan', NULL, NULL, 5, NULL, 0, 1, '2022-05-13 23:33:04', '2022-05-13 23:33:04', 2, 2);
INSERT INTO `dfl_type_item` VALUES (17, 6, 'i', 'IOS', NULL, NULL, NULL, 1, NULL, 0, 1, '2022-05-15 09:28:51', '2022-05-15 09:28:51', 2, 2);
INSERT INTO `dfl_type_item` VALUES (18, 6, 'a', '安卓', NULL, NULL, NULL, 2, NULL, 0, 1, '2022-05-15 09:29:01', '2022-05-15 09:29:01', 2, 2);
INSERT INTO `dfl_type_item` VALUES (19, 6, 'h', 'H5', NULL, NULL, NULL, 3, NULL, 0, 1, '2022-05-15 09:29:12', '2022-05-15 09:29:12', 2, 2);
INSERT INTO `dfl_type_item` VALUES (20, 6, 'w', '微信', NULL, NULL, NULL, 4, NULL, 0, 1, '2022-05-15 09:29:24', '2022-05-15 09:29:24', 2, 2);
INSERT INTO `dfl_type_item` VALUES (21, 6, 'p', 'PC', NULL, NULL, NULL, 5, NULL, 0, 1, '2022-05-15 09:29:39', '2022-05-15 09:29:39', 2, 2);
INSERT INTO `dfl_type_item` VALUES (22, 7, '1', '用户', NULL, NULL, NULL, 1, NULL, 0, 1, '2022-05-20 06:41:14', '2022-05-20 06:41:14', 2, 2);
INSERT INTO `dfl_type_item` VALUES (23, 7, '2', 'IP', NULL, NULL, NULL, 2, NULL, 0, 1, '2022-05-20 06:41:25', '2022-05-20 06:41:25', 2, 2);
INSERT INTO `dfl_type_item` VALUES (24, 7, '3', '设备号', NULL, NULL, NULL, 3, NULL, 0, 1, '2022-05-20 06:41:38', '2022-05-20 06:41:38', 2, 2);
INSERT INTO `dfl_type_item` VALUES (25, 7, '4', 'dataId', NULL, NULL, NULL, 4, NULL, 1, 1, '2022-05-20 06:41:52', '2022-05-21 06:19:17', 2, 2);
INSERT INTO `dfl_type_item` VALUES (26, 8, 'whiteIp', '白名单IP', NULL, NULL, NULL, 1, NULL, 0, 1, '2022-05-20 06:42:30', '2022-05-20 06:57:47', 2, 2);
INSERT INTO `dfl_type_item` VALUES (27, 8, 'whiteUser', '用户白名单', NULL, NULL, NULL, 2, NULL, 0, 1, '2022-05-20 06:58:03', '2022-05-20 06:58:03', 2, 2);
INSERT INTO `dfl_type_item` VALUES (28, 8, 'blackIp', 'IP黑名单', NULL, NULL, NULL, 10, NULL, 0, 1, '2022-05-20 06:58:21', '2022-05-20 06:58:21', 2, 2);
INSERT INTO `dfl_type_item` VALUES (29, 8, 'blackUser', '用户黑名单', NULL, NULL, NULL, 11, NULL, 0, 1, '2022-05-20 06:58:35', '2022-05-20 06:58:35', 2, 2);
INSERT INTO `dfl_type_item` VALUES (30, 8, 'blackDeviceId', '设备黑名单', NULL, NULL, NULL, 12, NULL, 0, 1, '2022-05-20 06:58:54', '2022-05-20 06:58:54', 2, 2);
INSERT INTO `dfl_type_item` VALUES (31, 2, 'etcdKv', 'etcdKv快速失败模式', NULL, NULL, NULL, 2, 'ETCD分布式锁，ifAbsent已存在时快速失败', 0, 1, '2022-05-21 06:08:38', '2022-05-21 06:10:46', 2, 2);
INSERT INTO `dfl_type_item` VALUES (32, 2, 'etcdLock', 'etcdLock同步模式', NULL, NULL, NULL, 3, 'ETCD分布式锁，同步模式，等待前面任务锁消失或完成，然后取到锁', 0, 1, '2022-05-21 06:08:47', '2022-05-21 06:10:20', 2, 2);
INSERT INTO `dfl_type_item` VALUES (33, 2, 'zk', 'zookeeper模式', NULL, NULL, NULL, 4, 'zookeeper模式', 0, 1, '2022-05-21 06:09:30', '2022-05-21 06:26:11', 2, 2);
INSERT INTO `dfl_type_item` VALUES (34, 9, '1', 'LIMIT访问次数', NULL, NULL, NULL, 1, '访问次数', 0, 1, '2022-08-13 07:46:35', '2022-08-13 07:55:50', 2, 2);
INSERT INTO `dfl_type_item` VALUES (35, 9, '2', 'LIMIT_IP同IP访问次数', NULL, NULL, NULL, 2, '同IP访问次数', 0, 1, '2022-08-13 07:47:39', '2022-08-13 07:49:14', 2, 2);
INSERT INTO `dfl_type_item` VALUES (36, 9, '3', 'USER_IP同用户多IP数', NULL, NULL, NULL, 3, '同用户多IP数限制', 0, 1, '2022-08-13 07:48:05', '2022-08-13 07:49:23', 2, 2);
INSERT INTO `dfl_type_item` VALUES (37, 9, '4', 'IP_USER同IP多用户数', NULL, NULL, NULL, 4, '同IP多用户数限制', 0, 1, '2022-08-13 07:48:20', '2022-08-13 07:49:29', 2, 2);
INSERT INTO `dfl_type_item` VALUES (38, 9, '5', 'RES_IP同资源ID同IP访问次数', NULL, NULL, NULL, 5, '同资源ID同IP访问次数限制', 0, 1, '2022-08-13 07:48:41', '2022-08-13 07:55:26', 2, 2);
INSERT INTO `dfl_type_item` VALUES (39, 9, '6', 'RES_USER同资源ID同用户访问次数', NULL, NULL, NULL, 6, '同资源ID用户访问次数限制', 0, 1, '2022-08-13 07:49:00', '2022-08-13 07:50:00', 2, 2);

-- ----------------------------
-- Table structure for dfl_user
-- ----------------------------
DROP TABLE IF EXISTS `dfl_user`;
CREATE TABLE `dfl_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `telephone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `pwd` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `status` tinyint NOT NULL COMMENT '状态:是否有效0无效，1有效',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `modify_time` datetime NULL DEFAULT NULL,
  `create_user` int NULL DEFAULT NULL,
  `modify_user` int NULL DEFAULT NULL,
  `register_ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sys_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_nickname`(`nickname`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_user
-- ----------------------------
INSERT INTO `dfl_user` VALUES (1, 'test', 'test', '123', '123', '$2a$10$RvKTCQHl5HuGP92v7KpbQekLV/fmOWV4ZlTstytqonIauiq9XyD8K', 0, 1, 'test', '2022-05-04 08:34:36', '2022-05-08 19:04:26', NULL, 2, NULL, NULL);
INSERT INTO `dfl_user` VALUES (2, 'admin', 'admin', NULL, NULL, '$2a$10$ATSOeW9GsNGLgB9Ld214UOWM0FcCoF1MRLNfY2CftI5HwE12LEU9i', 0, 1, 'test', '2022-05-04 08:35:24', '2022-05-09 23:00:30', NULL, 2, NULL, NULL);
INSERT INTO `dfl_user` VALUES (3, 't1', 't1', '134', 'asgard2023@outlook.com', NULL, 0, 1, 'test', '2022-05-08 09:24:17', '2022-05-25 07:28:12', -1, 2, NULL, NULL);
INSERT INTO `dfl_user` VALUES (4, 'user', 'user', '134', 'test@126.com', '$2a$10$5GV1lg6WAtivuClvBHXCy.poduq37nA8J2GG2H8H5cwxT1cmXrwFu', 0, 1, 'test', NULL, '2022-05-25 07:41:56', NULL, 2, NULL, NULL);

-- ----------------------------
-- Table structure for dfl_user_role
-- ----------------------------
DROP TABLE IF EXISTS `dfl_user_role`;
CREATE TABLE `dfl_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `ext_config` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '扩展参数配置json',
  `if_del` tinyint NOT NULL COMMENT '是否删除',
  `status` tinyint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `create_user` int NULL DEFAULT NULL COMMENT '创建人',
  `modify_user` int NULL DEFAULT NULL COMMENT '修改人',
  `permit_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限类型(admin,view,select,update,delete等)',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id`) USING BTREE,
  INDEX `idx_create`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dfl_user_role
-- ----------------------------
INSERT INTO `dfl_user_role` VALUES (1, 2, 1, '2022-05-05 20:46:57', '2025-12-01 20:47:00', NULL, 0, 1, NULL, '2022-05-14 06:01:29', NULL, 2, NULL, NULL);
INSERT INTO `dfl_user_role` VALUES (2, 4, 2, '2022-05-25 07:40:24', '2028-06-28 07:40:29', NULL, 0, 1, '2022-05-25 07:40:40', '2022-05-25 07:40:40', 2, 2, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
