package com.github.xuchen93.other.model;

import cn.hutool.core.util.StrUtil;
import com.github.xuchen93.other.annotation.PoiFieldInfo;
import lombok.Data;

/**
 *  @author：wangxuchen
 */
@Data
public class PoiFieldInfoModel {
	private String fieldName;
	private String aliasName;
	private int index;
	private int width;


	public PoiFieldInfoModel() {
	}

	public PoiFieldInfoModel(String fieldName, PoiFieldInfo fieldInfo) {
		this.fieldName = fieldName;
		this.aliasName = StrUtil.isBlank(fieldInfo.value())?fieldName:fieldInfo.value();
		this.index = fieldInfo.index();
		this.width = fieldInfo.width();
	}
}
