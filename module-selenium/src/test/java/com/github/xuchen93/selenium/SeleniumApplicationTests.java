package com.github.xuchen93.selenium;

import com.github.xuchen93.selenium.config.CommonSeleniumConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SeleniumApplicationTests {

    @Autowired
    CommonSeleniumConfig config;

    @Test
    void contextLoads() {
        System.out.println(config);
    }

}
