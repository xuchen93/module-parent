package com.github.xuchen93.other.util.poi;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExcelToHtmlUtil {

	private static final String[] bordesr = {"border-top:", "border-right:", "border-bottom:", "border-left:"};
	private static final String[] borderStyles = {"solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ",
			"solid ", "solid", "solid", "solid", "solid", "solid"};

	public static String getExcelInfo(Workbook wb) {
		if (wb instanceof XSSFWorkbook) {
			XSSFWorkbook xWb = (XSSFWorkbook) wb;
			return getExcelInfo(xWb, true);
		} else if (wb instanceof HSSFWorkbook) {
			HSSFWorkbook hWb = (HSSFWorkbook) wb;
			return getExcelInfo(hWb, true);
		}
		return "";
	}

	/**
	 * 获取表格数据转为html元素
	 *
	 * @param wb
	 * @param isWithStyle
	 * @return
	 */
	public static String getExcelInfo(Workbook wb, boolean isWithStyle) {

		StringBuilder sb = new StringBuilder();

		Sheet sheet = wb.getSheetAt(0);// 获取第一个Sheet的内容
		// 去掉表格中没有值的行，获取表格的实际的行数
		int lastRowNum = filterNullRow(sheet);
		Map<String, String> map[] = getRowSpanColSpanMap(sheet);
		sb.append("<table style='border-collapse:collapse;' width='100%'>");
		Row row = null; // 兼容
		Cell cell = null; // 兼容
		for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
			row = sheet.getRow(rowNum);
			if (row == null) {
				sb.append("<tr><td > &nbsp;</td></tr>");
				continue;
			}
			if (row.getZeroHeight()) {
				continue;
			}
			sb.append("<tr>");
			int lastColNum = row.getLastCellNum();
			for (int colNum = 0; colNum < lastColNum; colNum++) {
				cell = row.getCell(colNum);
				if (cell == null) { // 特殊情况 空白的单元格会返回null
					sb.append("<td>&nbsp;</td>");
					continue;
				}
				String stringValue = getCellValue(cell);
				// 如果是空值要进行计算判断是否有计算的结果值。
//				if (stringValue==null||"".equals(stringValue.trim())){
//					FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
//					stringValue = evaluator.evaluate(cell).getNumberValue()+"";
//				}
				if (map[0].containsKey(rowNum + "," + colNum)) {
					String pointString = map[0].get(rowNum + "," + colNum);
					map[0].remove(rowNum + "," + colNum);
					int bottomeRow = Integer.parseInt(pointString.split(",")[0]);
					int bottomeCol = Integer.parseInt(pointString.split(",")[1]);
					int rowSpan = bottomeRow - rowNum + 1;
					int colSpan = bottomeCol - colNum + 1;
					sb.append("<td rowspan= '" + rowSpan + "' colspan= '" + colSpan + "' ");
				} else if (map[1].containsKey(rowNum + "," + colNum)) {
					map[1].remove(rowNum + "," + colNum);
					continue;
				} else {
					sb.append("<td ");
				}
				// 判断是否需要样式
				if (isWithStyle) {
					dealExcelStyle(wb, sheet, cell, sb);// 处理单元格样式
				}
				sb.append(">");
				if (stringValue == null || "".equals(stringValue.trim())) {
					sb.append(" &nbsp; ");
				} else {
					// 将ascii码为160的空格转换为html下的空格（&nbsp;）
					sb.append(stringValue.replace(String.valueOf((char) 160), "&nbsp;"));
				}
				sb.append("</td>");
			}
			sb.append("</tr>");
		}

		sb.append("</table>");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String>[] getRowSpanColSpanMap(Sheet sheet) {
		Map<String, String> map0 = new HashMap<String, String>();
		Map<String, String> map1 = new HashMap<String, String>();
		int mergedNum = sheet.getNumMergedRegions();
		CellRangeAddress range;
		for (int i = 0; i < mergedNum; i++) {
			range = sheet.getMergedRegion(i);
			int topRow = range.getFirstRow();
			int topCol = range.getFirstColumn();
			int bottomRow = range.getLastRow();
			int bottomCol = range.getLastColumn();
			map0.put(topRow + "," + topCol, bottomRow + "," + bottomCol);
			int tempRow = topRow;
			while (tempRow <= bottomRow) {
				int tempCol = topCol;
				while (tempCol <= bottomCol) {
					map1.put(tempRow + "," + tempCol, "");
					tempCol++;
				}
				tempRow++;
			}
			map1.remove(topRow + "," + topCol);
		}
		@SuppressWarnings("rawtypes")
		Map[] map = {map0, map1};
		return map;
	}

	/**
	 * 获取表格单元格Cell内容
	 *
	 * @param cell
	 * @return
	 */
	private static String getCellValue(Cell cell) {
		String result;
		CellType type = cell.getCellType();
		switch (type) {
			case NUMERIC:// 数字类型
				if (DateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
					SimpleDateFormat sdf = null;
					if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
						sdf = new SimpleDateFormat("HH:mm");
					} else {// 日期
						sdf = new SimpleDateFormat("yyyy-MM-dd");
					}
					Date date = cell.getDateCellValue();
					result = sdf.format(date);
				} else if (cell.getCellStyle().getDataFormat() == 58) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = DateUtil.getJavaDate(value);
					result = sdf.format(date);
				} else {
					double value = cell.getNumericCellValue();
					if (value == 0) {
						result = "";
					} else {
						CellStyle style = cell.getCellStyle();
						String temp = style.getDataFormatString();
						if (temp.equals("General")) {
							result = NumberUtil.decimalFormat("#", value);
						} else if (temp.endsWith("%")) {
							//0.00%  0%  百分比
							result = NumberUtil.decimalFormat(temp, value);
						} else {
							//0.00_  小数
							result = NumberUtil.decimalFormat(temp.replace("_", "").replace("0", "#"), value);
						}
					}
				}
				break;
			case STRING:// String类型
				result = cell.getRichStringCellValue().toString();
				break;
			case BLANK:
				result = "";
				break;
			default:
				result = "";
				break;
		}
		return result;
	}

	/**
	 * 处理表格样式
	 *
	 * @param wb
	 * @param sheet
	 * @param cell
	 * @param sb
	 */
	private static void dealExcelStyle(Workbook wb, Sheet sheet, Cell cell, StringBuilder sb) {

		CellStyle cellStyle = cell.getCellStyle();
		if (cellStyle != null) {
			HorizontalAlignment alignment = cellStyle.getAlignment();
			sb.append("align='").append(convertAlignToHtml(alignment)).append("' ");// 单元格内容的水平对齐方式
			VerticalAlignment verticalAlignment = cellStyle.getVerticalAlignment();
			sb.append("valign='").append(convertVerticalAlignToHtml(verticalAlignment)).append("' ");// 单元格中内容的垂直排列方式
			if (wb instanceof XSSFWorkbook) {
				XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
				sb.append("style='");
				sb.append("font-weight:").append(xf.getBold() ? "bold" : "normal").append(";"); // 字体加粗
				sb.append("font-size: ").append(xf.getFontHeight() / 2).append("%;"); // 字体大小
				int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
				sb.append("width:").append(columnWidth).append("px;");
				XSSFColor xc = xf.getXSSFColor();
				if (xc != null) {
					sb.append("color:#").append(xc.getARGBHex().substring(2)).append(";"); // 字体颜色
				}

				XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
				if (bgColor != null) {
					sb.append("background-color:#").append(bgColor.getARGBHex().substring(2)).append(";"); // 背景颜色
				}
				sb.append(getBorderStyle(0, cellStyle.getBorderTop().getCode(),
						((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
				sb.append(getBorderStyle(1, cellStyle.getBorderRight().getCode(),
						((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
				sb.append(getBorderStyle(2, cellStyle.getBorderBottom().getCode(),
						((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
				sb.append(getBorderStyle(3, cellStyle.getBorderLeft().getCode(),
						((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));

			} else if (wb instanceof HSSFWorkbook) {

				HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
				short fontColor = hf.getColor();
				sb.append("style='");
				HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
				HSSFColor hc = palette.getColor(fontColor);
				sb.append("font-weight:").append(hf.getBold() ? "bold" : "normal").append(";"); // 字体加粗
				sb.append("font-size: ").append(hf.getFontHeight() / 2).append("%;"); // 字体大小
				String fontColorStr = convertToStardColor(hc);
				if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
					sb.append("color:").append(fontColorStr).append(";"); // 字体颜色
				}
				int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
				sb.append("width:").append(columnWidth).append("px;");
				short bgColor = cellStyle.getFillForegroundColor();
				hc = palette.getColor(bgColor);
				String bgColorStr = convertToStardColor(hc);
				if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
					sb.append("background-color:").append(bgColorStr).append(";"); // 背景颜色
				}
				sb.append(getBorderStyle(palette, 0, cellStyle.getBorderTop().getCode(), cellStyle.getTopBorderColor()));
				sb.append(getBorderStyle(palette, 1, cellStyle.getBorderRight().getCode(), cellStyle.getRightBorderColor()));
				sb.append(getBorderStyle(palette, 3, cellStyle.getBorderLeft().getCode(), cellStyle.getLeftBorderColor()));
				sb.append(getBorderStyle(palette, 2, cellStyle.getBorderBottom().getCode(), cellStyle.getBottomBorderColor()));
			}

			sb.append("' ");
		}
	}

	/**
	 * 单元格内容的水平对齐方式
	 *
	 * @param alignment
	 * @return
	 */
	private static String convertAlignToHtml(HorizontalAlignment alignment) {

		String align = "left";
		switch (alignment) {
			case LEFT:
				align = "left";
				break;
			case CENTER:
				align = "center";
				break;
			case RIGHT:
				align = "right";
				break;
			default:
				break;
		}
		return align;
	}

	/**
	 * 单元格中内容的垂直排列方式
	 *
	 * @param verticalAlignment
	 * @return
	 */
	private static String convertVerticalAlignToHtml(VerticalAlignment verticalAlignment) {

		String valign = "middle";
		switch (verticalAlignment) {
			case BOTTOM:
				valign = "bottom";
				break;
			case CENTER:
				valign = "center";
				break;
			case TOP:
				valign = "top";
				break;
			default:
				break;
		}
		return valign;
	}

	private static String convertToStardColor(HSSFColor hc) {

		StringBuffer sb = new StringBuffer("");
		if (hc != null) {
			if (HSSFColor.getMutableIndexHash().get(HSSFColor.HSSFColorPredefined.AUTOMATIC).getIndex() == hc.getIndex()) {
				return null;
			}
			sb.append("#");
			for (int i = 0; i < hc.getTriplet().length; i++) {
				sb.append(fillWithZero(Integer.toHexString(hc.getTriplet()[i])));
			}
		}

		return sb.toString();
	}

	private static String fillWithZero(String str) {
		if (str != null && str.length() < 2) {
			return "0" + str;
		}
		return str;
	}

	private static String getBorderStyle(HSSFPalette palette, int b, short s, short t) {
		if (s == 0)
			return bordesr[b] + borderStyles[s] + "#d0d7e5 1px;";
		String borderColorStr = convertToStardColor(palette.getColor(t));
		borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000" : borderColorStr;
		return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";

	}

	private static String getBorderStyle(int b, short s, XSSFColor xc) {

		if (s == 0)
			return bordesr[b] + borderStyles[s] + "#d0d7e5 1px;";
		if (xc != null && !"".equals(xc)) {
			String borderColorStr = xc.getARGBHex();// t.getARGBHex();
			borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000"
					: borderColorStr.substring(2);
			return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";
		}
		return "";
	}


	/**
	 * 功能描述：将sheet末位为空的行去掉，得到实际有数据的行数 输入参数：<按照参数定义顺序>
	 * <p>
	 * :Excel表单封装对象 返回值: 类型 <说明>
	 *
	 * @return
	 * @throws Exception
	 */
	private static int filterNullRow(Sheet childSheet) {
		int rowNum = childSheet.getLastRowNum();
		int j = 1;
		// 判断末行，如果不为空，直接返回行数
		Row lastRow = childSheet.getRow(rowNum);
		if (!isNullRow(lastRow))
			return rowNum;
		// 如果末行为空，则进入循环，直到遇到不为空的为止
		for (int i = rowNum - 1; i > 0; i--) {
			Row row = childSheet.getRow(i);
			if (row == null || isNullRow(row)) {
				j++;
			} else {
				break;
			}
		}
		return rowNum - j;
	}

	/**
	 * 功能描述：判断一行是否是空行，true 是 false 不是 输入参数：<按照参数定义顺序>
	 *
	 * @return boolean
	 * @throws Exception
	 */
	private static boolean isNullRow(Row row) {
		if (row == null)
			return true;
		boolean nullFlag = true;
		for (int k = 0; k < row.getLastCellNum(); k++) {
			Cell cell = row.getCell((short) k);
			if (!"".equals(transferToString(cell))) {
				nullFlag = false;
				break;
			}
		}
		return nullFlag;
	}

	/**
	 * 功能描述：处理cell中的值 输入参数：<按照参数定义顺序>
	 *
	 * @return String
	 * @throws Exception
	 */
	private static String transferToString(Cell cell) {
		String transferedStr = "";
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
			case NUMERIC: // 数字
				if (DateUtil.isCellDateFormatted(cell)) {
					// 如果是date类型则 ，获取该cell的date值
					transferedStr = DateUtil.getJavaDate(cell.getNumericCellValue()).toString();
				} else { // 纯数字
					double cellValue = cell.getNumericCellValue();
					transferedStr = NumberUtil.decimalFormat("#.##", cellValue);
				}
				break;
			case STRING: // 字符串
				transferedStr = cell.getStringCellValue() + "";
				break;
			default:
				transferedStr = "";
				break;
		}
		return transferedStr;
	}
}
