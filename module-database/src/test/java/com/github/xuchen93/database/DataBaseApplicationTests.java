package com.github.xuchen93.database;

import com.github.xuchen93.database.table.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class DataBaseApplicationTests {

	@Autowired
	SysUserService sysUserService;
	@Autowired
	DataSourceProperties properties;

	@Test
	void contextLoads() {
		log.info(properties.getUsername());
		log.info(properties.getPassword());
		log.info(properties.getUrl());
		log.info(properties.getDriverClassName());
		log.info(sysUserService.list().toString());
//        List<SysUser> list = sysUserService.listObjs(new QueryWrapper(new SysUser() {{
//            setNickName("nickname5");
//        }}));
//        Page<SysUser> userPage = sysUserService.page(new Page<>(), new QueryWrapper<>());

	}


}
