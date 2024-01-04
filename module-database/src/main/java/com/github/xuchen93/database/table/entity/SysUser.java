package com.github.xuchen93.database.table.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.xuchen93.database.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 用户昵称
	 */
	private String nickName;
	/**
	 * 密码
	 */
	private String password;
}
