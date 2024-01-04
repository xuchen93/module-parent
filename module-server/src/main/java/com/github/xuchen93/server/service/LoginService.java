package com.github.xuchen93.server.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xuchen93.database.table.entity.SysUser;
import com.github.xuchen93.database.table.service.SysUserService;
import com.github.xuchen93.model.ex.BusiException;
import com.github.xuchen93.web.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginService {

	@Autowired
	SysUserService sysUserService;
	@Autowired
	JwtService jwtService;

	public String login(SysUser user) {
		SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("user_name", user.getUserName()));
		if (sysUser == null) {
			throw new BusiException("用户不存在");
		}
		String checkPwd = SecureUtil.md5(user.getPassword());
		if (ObjectUtil.notEqual(sysUser.getPassword(), checkPwd)) {
			throw new BusiException("密码不正确");
		}
		return jwtService.generateToken(sysUser);
	}
}
