package com.github.xuchen93.server.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.xuchen93.web.common.RequestContextProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Slf4j
@Configuration
public class MybatisPlusServerConfig {

	/**
	 * 字段填充
	 */
	@Bean
	public MetaObjectHandler metaObjectHandler() {
		return new MetaObjectHandler() {
			@Override
			public void insertFill(MetaObject metaObject) {
				log.debug("【insert】执行字段自动填充");
				this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
				this.strictInsertFill(metaObject, "createUser", String.class, RequestContextProxy.getUserName());
			}

			@Override
			public void updateFill(MetaObject metaObject) {
				log.debug("【update】执行字段自动填充");
				this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
				this.strictInsertFill(metaObject, "updateUser", String.class, RequestContextProxy.getUserName());
			}
		};
	}
}
