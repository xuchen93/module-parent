package com.github.xuchen93.server.controller;

import cn.hutool.core.util.StrUtil;
import com.github.xuchen93.database.table.entity.SysUser;
import com.github.xuchen93.model.R;
import com.github.xuchen93.model.ex.BusiException;
import com.github.xuchen93.server.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	@Autowired
	LoginService loginService;

	/**
	 * 登录
	 */
	@PostMapping("login")
	public R login(@RequestBody SysUser user) {
		if (StrUtil.hasBlank(user.getUserName(), user.getPassword())) {
			throw new BusiException("缺少用户名或者密码");
		}
		String token = loginService.login(user);
		return R.success(token);
	}
}
