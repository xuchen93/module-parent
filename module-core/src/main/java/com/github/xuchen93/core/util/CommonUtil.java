package com.github.xuchen93.core.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.xuchen93.core.model.log.LogConfig;
import com.github.xuchen93.core.model.log.SimpleLayout;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@Slf4j
@Data
public class CommonUtil {

	/**
	 * 控制台输出model对象的mybatis resultMap配置
	 * 约定：
	 * 驼峰映射
	 * 只包含简单字段和List两种字段类型
	 */
	public static void soutModelXml(Class cl) {
		List<String> list = generateModelXml(cl);
		for (String s : list) {
			System.out.println(s);
		}
	}

	/**
	 * 控制台输出mybatis addList的insert
	 */
	public static void soutAddListEntity(Class cl) {
		Set<String> set = new HashSet<>();
		set.add("serialVersionUID");
		Field[] fields = ReflectUtil.getFields(cl);
		System.out.println("\t<insert id =\"addList\" parameterType=\"java.util.List\" >");
		System.out.println("\t\tinsert into XXX");
		System.out.print("\t\t(");
		StringBuilder sb = new StringBuilder(100);
		for (Field field : fields) {
			if (set.contains(field.getName())) {
				continue;
			}
			sb.append(StrUtil.toSymbolCase(field.getName(), '_') + ",");
		}
		System.out.print(sb.deleteCharAt(sb.length() - 1));
		System.out.println(")");
		System.out.println("\t\tvalues");
		System.out.println("\t\t<foreach collection =\"list\" item=\"item\" index= \"index\" separator =\",\">");
		System.out.print("\t\t(");
		sb = new StringBuilder(300);
		for (Field field : fields) {
			if (set.contains(field.getName())) {
				continue;
			}
			if ("version".equals(field.getName())) {
				sb.append("0,");
			} else {
				sb.append(String.format("#{item.%s},", field.getName()));
			}
		}
		System.out.print(sb.deleteCharAt(sb.length() - 1));
		System.out.println(")");
		System.out.println("\t\t</foreach>");
		System.out.println("\t</insert>");
	}

	@SneakyThrows
	private static List<String> generateModelXml(Class cl) {
		List<String> result = new ArrayList<>(100);
		Field[] fields = ReflectUtil.getFields(cl);
		List<Class> fieldClassList = new ArrayList<>(10);
		List<String> tempList = new ArrayList<>(10);
		result.add(StrUtil.format("\t<resultMap id=\"{}\" type=\"{}\">", StrUtil.lowerFirst(cl.getSimpleName()), cl.getCanonicalName()));
		for (Field field : fields) {
			if ("serialVersionUID".equals(field.getName())) {
				continue;
			}
			if (Collection.class.isAssignableFrom(field.getType())) {
				String className = StrUtil.subBetween(field.getGenericType().toString(), "<", ">");
				if (className == null) {//该list没有泛型
					tempList.add(StrUtil.format("\t\t<collection  property=\"{}\" resultType=\"{}\"/>", field.getName(), "xxx.xxx"));
					continue;
				}
				List<String> list = StrUtil.split(className, '.');
				fieldClassList.add(Class.forName(className));
				tempList.add(StrUtil.format("\t\t<collection  property=\"{}\" resultMap=\"{}\"/>", field.getName(), StrUtil.lowerFirst(list.get(list.size() - 1))));
			} else {
				result.add(StrUtil.format("\t\t<result column=\"{}\" property=\"{}\"/>", StrUtil.toUnderlineCase(field.getName()), field.getName()));
			}
		}
		result.addAll(tempList);
		result.add("\t</resultMap>");
		result.add("");
		for (Class aClass : fieldClassList) {
			result.addAll(generateModelXml(aClass));
		}
		return result;
	}

	/**
	 * 获取对象的json，含null值，一般是提供接口文档时使用
	 */
	public static JSONObject getJsonStrWithNullValue(Object obj) {
		JSONConfig jsonConfig = JSONConfig.create()
				.setOrder(true)
				.setIgnoreNullValue(false);
		Field[] fields = ReflectUtil.getFields(obj.getClass());
		//尝试给第一级的list泛型赋值
		Arrays.stream(fields)
				.filter(i -> i.getType().isAssignableFrom(List.class) && ReflectUtil.getFieldValue(obj, i.getName()) == null)
				.forEach(type -> {
					if (type.getGenericType() instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) type.getGenericType();
						try {
							Object o = Class.forName(parameterizedType.getActualTypeArguments()[0].getTypeName()).newInstance();
							ReflectUtil.setFieldValue(obj, type, CollUtil.newArrayList(o));
						} catch (Exception e) {
							ReflectUtil.setFieldValue(obj, type, Collections.emptyList());
						}
					} else {
						ReflectUtil.setFieldValue(obj, type, Collections.emptyList());
					}
				});
		//尝试给第一级的Map泛型赋值
		Arrays.stream(fields)
				.filter(i -> i.getType().isAssignableFrom(Map.class) && ReflectUtil.getFieldValue(obj, i.getName()) == null)
				.forEach(type -> {
					if (type.getGenericType() instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) type.getGenericType();
						try {
							Object value = SingleInstance.DEFAULT_TYPE_MAP.get(parameterizedType.getActualTypeArguments()[1].getTypeName());
							ReflectUtil.setFieldValue(obj, type, MapBuilder.create()
									.put(SingleInstance.DEFAULT_TYPE_MAP.getOrDefault(parameterizedType.getActualTypeArguments()[0].getTypeName(), "defaultKey")
											, value == null ? Class.forName(parameterizedType.getActualTypeArguments()[1].getTypeName()).newInstance() : value)
									.build());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		return JSONUtil.parseObj(obj, jsonConfig);
	}

	public static void tryLogPrettyStr(String json) {
		if (JSONUtil.isJson(json)) {
			log.info(JSONUtil.toJsonPrettyStr(json));
		} else {
			log.error(json);
		}
	}



	private static final class SingleInstance {
		private static final Map<String, Object> DEFAULT_TYPE_MAP = MapBuilder.<String, Object>create()
				.put("java.lang.Byte", 2)
				.put("java.lang.Short", 2)
				.put("java.lang.Integer", 2)
				.put("java.lang.Long", 2L)
				.put("java.lang.Float", 1.2F)
				.put("java.lang.Double", 1.2D)
				.put("java.lang.String", "defaultStr")
				.build();
	}


	public static void formatSimpleLog(LogConfig logConfig) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.getLoggerList().forEach(i -> {
			i.setLevel(Level.INFO);
			i.iteratorForAppenders().forEachRemaining(j -> {
				ConsoleAppender consoleAppender = (ConsoleAppender) j;
				LayoutWrappingEncoder encoder = (LayoutWrappingEncoder) consoleAppender.getEncoder();
				SimpleLayout simpleLayout = new SimpleLayout(logConfig);
				simpleLayout.setContext(loggerContext);
				simpleLayout.start();
				encoder.setLayout(simpleLayout);
			});
		});
	}
}
