package com.github.xuchen93.database.table.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.xuchen93.database.table.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

	void create(SysUser user);
}
