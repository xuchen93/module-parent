package com.github.xuchen93.selenium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.github.xuchen93")
public class SeleniumApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeleniumApplication.class, args);
	}

}
