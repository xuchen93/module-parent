package com.github.xuchen93.web.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ObjectMapperConfig {

	@Bean
	public ObjectMapper objectMapper() {
		SimpleModule javaTimeModule = new JavaTimeModule()
				.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)))
				.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
		return new ObjectMapper()
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.registerModule(javaTimeModule)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
	}
}
