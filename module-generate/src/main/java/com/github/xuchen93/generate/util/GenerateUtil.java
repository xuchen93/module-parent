package com.github.xuchen93.generate.util;


import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.github.xuchen93.generate.model.GenerateInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class GenerateUtil {

	public static void generate(GenerateInfo generateInfo) {
		FastAutoGenerator.create(generateInfo.getDbUrl(), generateInfo.getDbUserName(), generateInfo.getDbPassword())
				.globalConfig(builder -> {
					builder.author(generateInfo.getAuthor()) // 设置作者
							.outputDir(generateInfo.getProjectPath())
							.disableOpenDir()
							.dateType(generateInfo.getDateType())
					;
				})
				.packageConfig(builder -> {
					builder.parent(generateInfo.getPackageName()) // 设置父包名
							.moduleName(generateInfo.getModule()) // 设置父包模块名
							.pathInfo(Collections.singletonMap(OutputFile.xml, generateInfo.getXmlPath())); // 设置mapperXml生成路径
				})
				.strategyConfig(builder -> {
					builder.addInclude(generateInfo.getTableList()) // 设置需要生成的表名
							.entityBuilder().versionColumnName(generateInfo.getVersion()).versionPropertyName(generateInfo.getVersion())
							.controllerBuilder().enableRestStyle();
					if (generateInfo.isLombok()) builder.entityBuilder().enableLombok();
					if (generateInfo.getSuperEntityClass() != null) {
						builder.entityBuilder().superClass(generateInfo.getSuperEntityClass());
						builder.entityBuilder().addSuperEntityColumns(Arrays.stream(ReflectUtil.getFields(generateInfo.getSuperEntityClass())).map(i-> StrUtil.toUnderlineCase(i.getName())).collect(Collectors.toList()));
					}
					if (generateInfo.getSuperControllerClass() != null)
						builder.controllerBuilder().superClass(generateInfo.getSuperControllerClass());
					if (generateInfo.isEnableFileOverride()){
						builder.entityBuilder().enableFileOverride()
							.serviceBuilder().enableFileOverride()
							.mapperBuilder().enableFileOverride()
							.controllerBuilder().enableFileOverride();
					}
				})
				.execute();
	}
}
