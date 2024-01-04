package com.github.xuchen93.server;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.github.xuchen93.core.config.XuchenProperties;
import com.github.xuchen93.web.common.SimpleHttpHandle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ServerApplicationTests {

	@Autowired
	XuchenProperties properties;

	@Test
	void contextLoads() {
		System.out.println(properties);
		System.out.println(new SimpleHttpHandle().hello());
		ThreadUtil.sleep(1000);
	}

}
