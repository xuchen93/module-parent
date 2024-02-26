package com.github.xuchen93.core.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.*;
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

	public static String toJsonPrettyStr(Object obj){
		JSON json = JSONUtil.parse(obj, JSONConfigSingleton.JSON_CONFIG_WITH_DATAFORMAT);
		formatJSONDate(json);
		return JSONUtil.toJsonPrettyStr(json);
	}

	public static String toJsonStr(Object obj){
		if (obj == null) {
			return null;
		}
		if (obj instanceof CharSequence) {
			return StrUtil.str((CharSequence) obj);
		}
		JSON json = JSONUtil.parse(obj, JSONConfigSingleton.JSON_CONFIG_WITH_DATAFORMAT);
		formatJSONDate(json);
		return JSONUtil.toJsonStr(json);
	}

	private static class JSONConfigSingleton {
		private static final JSONConfig JSON_CONFIG_WITH_DATAFORMAT = JSONConfig.create().setDateFormat(DatePattern.NORM_DATETIME_PATTERN);
	}

	private static void formatJSONDate(JSON json){
		if (json instanceof JSONArray){
			JSONArray jsonArray = (JSONArray) json;
			for (Object j : jsonArray) {
				if (j instanceof JSON){
					formatJSONDate((JSON) j);
				}
			}
		} else {
			JSONObject jsonObject = (JSONObject) json;
			for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
				if (entry.getValue() instanceof Long && entry.getValue().toString().length() == 13){
					jsonObject.set(entry.getKey(),new Date((long)entry.getValue()));
				}
				if (entry.getValue() instanceof JSON){
					formatJSONDate((JSON) entry.getValue());
				}
			}
		}
	}
}
