package com.github.xuchen93.core.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xuchen.wang
 * @date 2023/9/26
 */
@Data
@Slf4j
public class BeanUtil {

	public static <T> List<T> handleNavicatJson(String jsonFilePath, Class<T> tClass) {
		JSONArray records = handleNavicatJson(jsonFilePath);
		return records.stream().map(i -> {
			JSONObject obj = (JSONObject) i;
			return obj.toBean(tClass);
		}).collect(Collectors.toList());
	}

	public static JSONArray handleNavicatJson(String jsonFilePath) {
		String string = FileUtil.readUtf8String(jsonFilePath);
		JSONObject jsonObject = JSONUtil.parseObj(string);
		return jsonObject.getJSONArray("RECORDS");
	}
}
