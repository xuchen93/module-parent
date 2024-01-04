package com.github.xuchen93.other.util.poi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.sax.handler.AbstractRowHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xuchen.wang
 * @date 2021/7/22
 */
public class BeanRowExchangeHandler<T> extends AbstractRowHandler<T> {

	/**
	 * 标题所在行（从0开始计数）
	 */
	private final int headerRowIndex;
	/**
	 * 标题行
	 */
	private List<List<Object>> headerListList;

	private List<T> result;

	private Class<T> tClass;

	private CopyOptions copyOptions;

	/**
	 * 构造
	 *
	 * @param headerRowIndex 标题所在行（从0开始计数）
	 * @param startRowIndex  读取起始行（包含，从0开始计数）
	 * @param clazz          Bean类型
	 */
	public BeanRowExchangeHandler(int headerRowIndex, int startRowIndex, Class<T> clazz) {
		this(headerRowIndex, startRowIndex, Integer.MAX_VALUE, clazz);
	}

	/**
	 * 构造
	 *
	 * @param headerRowIndex 标题所在行（从0开始计数）
	 * @param startRowIndex  读取起始行（包含，从0开始计数）
	 * @param endRowIndex    读取结束行（包含，从0开始计数）
	 * @param clazz          Bean类型
	 */
	public BeanRowExchangeHandler(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> clazz) {
		super(startRowIndex, endRowIndex);
		Assert.isTrue(headerRowIndex <= startRowIndex, "headerRowIndex must <= startRowIndex");
		Assert.isTrue(headerRowIndex >= 0, "headerRowIndex row must >= 0");
		this.headerRowIndex = headerRowIndex;
		this.tClass = clazz;
		this.copyOptions = CopyOptions.create();
		this.result = new ArrayList<>();
		this.headerListList = new ArrayList<>();
		this.convertFunc = (rowList) -> BeanUtil.toBean(IterUtil.toMap(headerListList.get(0), rowList)
				, clazz
				, copyOptions);
	}

	@Override
	public void handle(int sheetIndex, long rowIndex, List<Object> rowList) {
		if (rowIndex >= headerRowIndex && rowIndex < startRowIndex) {
			headerListList.add(rowList);
		} else {
			super.handle(sheetIndex, rowIndex, rowList);
		}
	}

	@Override
	public void handleData(int sheetIndex, long rowIndex, T data) {
		result.add(data);
	}

	/**
	 * 自定义映射函数，预留
	 */
	public void setConvertFunc(Func1<List<Object>, T> convertFunc) {
		super.convertFunc = convertFunc;
	}

	/**
	 * 按照实体类字段顺序映射
	 */
	public void setFieldSortMapper() {
		Field[] fields = ReflectUtil.getFields(tClass);
		this.setConvertFunc((Func1<List<Object>, T>) rowList -> {
			T t = ReflectUtil.newInstance(tClass);
			for (int i = 0; i < rowList.size(); i++) {
				ReflectUtil.setFieldValue(t, fields[i], rowList.get(i));
			}
			return t;
		});
	}

	/**
	 * 修改默认映射中的BeanUtil.toBean的参数
	 * @param copyOptions
	 */
	public void setCopyOptions(CopyOptions copyOptions){
		this.copyOptions = copyOptions;
	}

	/**
	 * 获取结果集
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * 获取标题行集
	 *
	 * @return
	 */
	public List<List<Object>> getHeaderListList() {
		return headerListList;
	}
}
