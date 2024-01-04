package com.github.xuchen93.generate.model;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.List;

@Data
public class GenerateInfo {
	//-----------------global配置---------------------
	/**
	 * 作者
	 */
	private String author = "system";
	/**
	 * 时间类型
	 */
	private DateType dateType = DateType.TIME_PACK;
	//-----------------策略配置---------------------
	/**
	 * 开启lombok
	 */
	private boolean lombok = true;
	/**
	 * 表名
	 */
	private List<String> tableList;
	private boolean enableFileOverride;
	/**
	 * entity父类
	 */
	private Class superEntityClass = null;
	/**
	 * 乐观锁字段
	 */
	private String version;
	/**
	 * 逻辑删字段
	 */
	private String logicDelete;
	/**
	 * controller父类
	 */
	private Class superControllerClass = null;
	//-----------------package---------------------
	/**
	 * 模块目录，会自动更新项目路径
	 */
	private String module;
	/**
	 * 项目路径，默认当前根目录下的/src/main/java
	 */
	private String projectPath = System.getProperty("user.dir") + "/src/main/java";
	/**
	 * 包路径 如com.github.xuchen93.generate
	 */
	private String packageName;
	/**
	 * Mapper XML 包名,默认值:mapper.xml
	 */
	private String xmlPath = "src/main/resources/mapper";
	//-----------------datasource配置---------------------
	/**
	 * 数据库url
	 */
	private String dbUrl;
	/**
	 * 数据库username
	 */
	private String dbUserName;
	/**
	 * 数据库密码
	 */
	private String dbPassword;

	/**
	 * 多模块时用
	 *
	 * @param module 模块名
	 */
	public void setModule(String module) {
		this.module = module;
		this.projectPath = StrUtil.format(System.getProperty("user.dir") + StrUtil.format("/{}/src/main/java", module));
	}

	public void setDBProperties(DataSourceProperties dataSourceProperties) {
		dbUrl = dataSourceProperties.getUrl();
		dbUserName = dataSourceProperties.getUsername();
		dbPassword = dataSourceProperties.getPassword();
	}
}
