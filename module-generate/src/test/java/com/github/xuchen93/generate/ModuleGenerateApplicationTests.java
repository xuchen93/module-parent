package com.github.xuchen93.generate;

import cn.hutool.core.collection.CollUtil;
import com.github.xuchen93.database.base.BaseEntity;
import com.github.xuchen93.generate.model.GenerateInfo;
import com.github.xuchen93.generate.service.GenerateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ModuleGenerateApplicationTests {

	@Autowired
	GenerateService generateService;

	@Test
	void contextLoads() {
		generateService.generate(new GenerateInfo(){{
			setTableList(CollUtil.newArrayList("sys_user"));
			setVersion("version");
			setSuperEntityClass(BaseEntity.class);

			setPackageName("com.github.xuchen93.generate.temp");
		}});
	}

}
