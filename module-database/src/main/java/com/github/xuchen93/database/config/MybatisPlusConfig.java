package com.github.xuchen93.database.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@MapperScan("com.github.xuchen93.database.table.dao")
public class MybatisPlusConfig {

	/**
	 * mybatis-plus分页插件
	 */
	@Bean
	public PaginationInnerInterceptor paginationInterceptor() {
		return new PaginationInnerInterceptor();
	}

	/**
	 * id生成策略
	 */
	@Bean
	@ConditionalOnMissingBean(IdentifierGenerator.class)
	public IdentifierGenerator idGenerator() {
		return new DefaultIdentifierGenerator();
	}

	/**
	 * 乐观锁插件
	 */
	@Bean
	public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
		return new OptimisticLockerInnerInterceptor();
	}

	/**
	 * 字段填充
	 */
	@Bean
	@ConditionalOnMissingBean(MetaObjectHandler.class)
	public MetaObjectHandler metaObjectHandler() {
		return new MetaObjectHandler() {
			@Override
			public void insertFill(MetaObject metaObject) {
				if (log.isDebugEnabled()) {
					log.debug("【insert】执行字段自动填充");
				}
				this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
			}

			@Override
			public void updateFill(MetaObject metaObject) {
				if (log.isDebugEnabled()) {
					log.debug("【update】执行字段自动填充");
				}
				this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
			}
		};
	}
}
