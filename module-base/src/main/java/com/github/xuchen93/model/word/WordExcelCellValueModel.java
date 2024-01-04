package com.github.xuchen93.model.word;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改word中表格数据模型
 * 参考自：https://www.jianshu.com/p/603108a48088
 *
 * @author edwin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordExcelCellValueModel {
	/**
	 * 第几个表格
	 */
	private int tableIndex;
	/**
	 * 第几行
	 */
	private int rowIndex;
	/**
	 * 第几列
	 */
	private int cellIndex;
	/**
	 * 文本值
	 */
	private String value;

	public WordExcelCellValueModel(int tableIndex, int rowIndex, int cellIndex) {
		this(tableIndex, rowIndex, cellIndex, "");
	}
}
