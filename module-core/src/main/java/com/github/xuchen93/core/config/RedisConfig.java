package com.github.xuchen93.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.redis", name = "host")
public class RedisConfig {

	@Bean("redisTemplate")
	public RedisTemplate<String, Object> getRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		template.setKeySerializer(stringRedisSerializer);
		template.setHashKeySerializer(stringRedisSerializer);
		GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
		template.setValueSerializer(redisSerializer);
		template.setHashValueSerializer(redisSerializer);
		log.info("【xuchen-module-core】注入【redisTemplate】");
		return template;
	}

	@Bean
	@ConditionalOnMissingBean(RedisLockRegistry.class)
	public RedisLockRegistry redisLockRegistry(LettuceConnectionFactory redisConnectionFactory) {
		return new RedisLockRegistry(redisConnectionFactory, "RedisLock:");
	}
}
