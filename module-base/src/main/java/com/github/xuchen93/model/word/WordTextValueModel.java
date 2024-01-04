package com.github.xuchen93.model.word;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 修改word中段落字段值
 * 参考自：https://blog.csdn.net/yangbaggio/article/details/106436341
 *
 * @author edwin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordTextValueModel {
	/**
	 * paragraph索引
	 */
	private int paragraphIndex;
	/**
	 * run索引
	 */
	private int runIndex;
	/**
	 * 占位字符串
	 */
	private String placeholder;
	/**
	 * 替换的结果值
	 */
	private String value;

	public WordTextValueModel(int paragraphIndex, int runIndex, String value) {
		this(paragraphIndex, runIndex, "占位", value);
	}

	public WordTextValueModel(int paragraphIndex, int runIndex) {
		this(paragraphIndex, runIndex, "");
	}
}
