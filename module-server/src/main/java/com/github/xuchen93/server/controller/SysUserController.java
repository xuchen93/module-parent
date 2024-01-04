package com.github.xuchen93.server.controller;

import cn.hutool.core.util.StrUtil;
import com.github.xuchen93.database.table.entity.SysUser;
import com.github.xuchen93.database.table.service.SysUserService;
import com.github.xuchen93.model.R;
import com.github.xuchen93.model.ex.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sys/user")
public class SysUserController {

	@Autowired
	SysUserService sysUserService;

	/**
	 * 创建用户
	 */
	@PostMapping("create")
	public R login(@RequestBody SysUser user) {
		if (StrUtil.hasBlank(user.getUserName(), user.getNickName(), user.getPassword())) {
			throw new BusiException("缺少用户信息");
		}
		sysUserService.create(user);
		return R.success();
	}
}
