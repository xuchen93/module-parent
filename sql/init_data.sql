CREATE TABLE `sys_user`  (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_name` varchar(32) NULL COMMENT '用户名',
    `nick_name` varchar(32) NULL COMMENT '昵称',
    `password` varchar(255) NULL COMMENT '密码',
    `create_user` varchar(32) NULL COMMENT '创建者',
    `create_time` datetime NULL COMMENT '创建时间',
    `update_user` varchar(32) NULL COMMENT '更新者',
    `update_time` datetime NULL COMMENT '更新时间',
    `version` bigint NULL DEFAULT 0 COMMENT '版本',
    PRIMARY KEY (`id`)
);
ALTER TABLE `sys_user`  ADD UNIQUE INDEX `idx_user_name`(`user_name`) USING BTREE;
