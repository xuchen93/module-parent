package com.github.xuchen93.database.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础entity类
 */
@Data
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.ASSIGN_ID)
	protected Long id;
	@TableField(fill = FieldFill.INSERT)
	protected String createUser;
	@TableField(fill = FieldFill.INSERT)
	protected LocalDateTime createTime;
	@TableField(fill = FieldFill.UPDATE)
	protected String updateUser;
	@TableField(fill = FieldFill.UPDATE)
	protected LocalDateTime updateTime;
	@Version
	protected Long version;
}
