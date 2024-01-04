package pers.xuchen.module.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.xuchen.module.core.config.XuchenProperties;

@SpringBootTest
@Slf4j
class WebApplicationTests {

    @Autowired
    XuchenProperties properties;

    @Test
    void contextLoads() {
        System.out.println(properties);
    }

}
