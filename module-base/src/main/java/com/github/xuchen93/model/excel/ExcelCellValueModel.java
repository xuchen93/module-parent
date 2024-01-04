package com.github.xuchen93.model.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author edwin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelCellValueModel {
	private int rowIndex;
	private int columnIndex;
	private double value;
}
