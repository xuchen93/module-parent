package com.github.xuchen93.other.util.poi;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.xuchen93.model.excel.ExcelCellValueModel;
import com.github.xuchen93.model.word.WordExcelCellValueModel;
import com.github.xuchen93.model.word.WordTextValueModel;
import com.github.xuchen93.other.annotation.PoiFieldInfo;
import com.github.xuchen93.other.model.PoiFieldInfoModel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PoiUtil {

	/**
	 * 更新word中表格值
	 */
	public static void updateWordTableInfo(XWPFDocument doc, List<WordExcelCellValueModel> list) {
		list.forEach(item -> {
			doc.getTableArray(item.getTableIndex()).getCTTbl()
					.getTrArray(item.getRowIndex()).getTcArray(item.getCellIndex())
					.getPArray(0).getRArray(0).getTArray(0).setStringValue(item.getValue());
		});
	}

	/**
	 * 更新word中文本值
	 */
	public static void updateWordTextInfo(XWPFDocument doc, List<WordTextValueModel> list) {
		list.forEach(item -> {
			XWPFRun run = doc.getParagraphArray(item.getParagraphIndex())
					.getRuns().get(item.getRunIndex());
			String text = run.getText(0);
			run.setText(text.replace(item.getPlaceholder(), item.getValue()), 0);
		});
	}

	/**
	 * 向excel中插入行数据
	 *
	 * @param rowData           插入的行数据
	 * @param startRow          插入行，对应excel行数的下一行
	 * @param fillPreviousValue 如果某个值为空，则从原excel上一行获取数据
	 */
	@SuppressWarnings("rawtypes")
	public static void insertExcel(List<List> rowData, int startRow, boolean fillPreviousValue, XSSFSheet sheet) {
		sheet.shiftRows(startRow, sheet.getLastRowNum(), rowData.size(), true, false);
		for (int i = 0; i < rowData.size(); i++) {
			XSSFRow newRow = sheet.createRow(startRow + i);
			for (int columnIndex = 0; columnIndex < rowData.get(i).size(); columnIndex++) {
				XSSFCell cell = newRow.createCell(columnIndex);
				Object o = rowData.get(i).get(columnIndex);
				if (o == null) {
					if (fillPreviousValue) {
						cell.setCellValue(sheet.getRow(startRow - 1).getCell(columnIndex).getStringCellValue());
					} else {
						cell.setCellValue("");
					}
				} else {
					if (o instanceof String) {
						cell.setCellValue((String) o);
					} else {
						cell.setCellValue(Double.parseDouble(String.valueOf(o)));
					}
				}
				cell.setCellStyle(sheet.getRow(startRow - 1).getCell(columnIndex).getCellStyle());
			}
		}
	}

	/**
	 * 给单元格赋值
	 */
	public static void setCellValue(List<ExcelCellValueModel> valueModelList, XSSFSheet sheet) {
		for (ExcelCellValueModel model : valueModelList) {
			sheet.getRow(model.getRowIndex()).getCell(model.getColumnIndex()).setCellValue(model.getValue());
		}
	}


	public static List<PoiFieldInfoModel> getFieldInfoList(Class c) {
		List<PoiFieldInfoModel> list = Arrays.stream(ReflectUtil.getFields(c)).filter(field -> field.isAnnotationPresent(PoiFieldInfo.class)).map(field -> {
			PoiFieldInfo annotation = field.getAnnotation(PoiFieldInfo.class);
			return new PoiFieldInfoModel(field.getName(), annotation);
		}).collect(Collectors.toList());
		List<PoiFieldInfoModel> noIndexList = list.stream().filter(i -> i.getIndex() == -1).collect(Collectors.toList());
		List<PoiFieldInfoModel> withIndexList = list.stream().filter(i -> i.getIndex() != -1).collect(Collectors.toList());
		Set<Integer> set = withIndexList.stream().map(PoiFieldInfoModel::getIndex).collect(Collectors.toSet());
		if (set.size() != withIndexList.size()) throw new RuntimeException("index值不可重复");
		PoiFieldInfoModel[] infoModels = new PoiFieldInfoModel[list.size()];
		Arrays.fill(infoModels,null);
		withIndexList.forEach(i->infoModels[i.getIndex()] = i);
		int noIndexListIndex = 0;
		for (int i = 0; i < list.size(); i++) {
			if (infoModels[i] == null) {
				PoiFieldInfoModel model = noIndexList.get(noIndexListIndex++);
				model.setIndex(i);
				infoModels[i] = model;
			}
		}
		return Arrays.asList(infoModels);
	}

	public static void handlerExcelWriter(List<PoiFieldInfoModel> infoModelList, ExcelWriter excelWriter){
		excelWriter.setHeaderAlias(getHeaderAlias(infoModelList));
		excelWriter.setOnlyAlias(true);
		//宽度
		infoModelList.forEach(i->excelWriter.setColumnWidth(i.getIndex(),i.getWidth()));
	}

	public static Map<String, String> getHeaderAlias(List<PoiFieldInfoModel> infoModelList){
		return infoModelList.stream().collect(Collectors.toMap(PoiFieldInfoModel::getFieldName, PoiFieldInfoModel::getAliasName, (o1, o2) -> o1, LinkedHashMap::new));
	}

}
