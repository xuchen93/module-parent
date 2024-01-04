package com.github.xuchen93.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public abstract class BaseController {

	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;

	/**
	 * 文件全名
	 *
	 * @param fileName
	 */
	protected void setDownloadType(String fileName) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
	}
}
