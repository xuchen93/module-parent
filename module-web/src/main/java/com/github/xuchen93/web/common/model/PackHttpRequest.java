package com.github.xuchen93.web.common.model;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;

public class PackHttpRequest extends HttpRequest {

	private boolean logHeader = false;

	public PackHttpRequest(String url) {
		super(url);
	}

	@Override
	public HttpResponse execute() {
		System.out.println(toString());
		return super.execute();
	}

	@Override
	public HttpResponse executeAsync() {
		System.out.println(toString());
		return super.execute(true);
	}

	@Override
	public HttpRequest method(Method method) {
		super.method(method);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("请求类型：").append(getMethod().name()).append(StrUtil.CRLF);
		if (super.form() != null) {
			sb.append("请求表单：").append(StrUtil.CRLF).append(JSONUtil.toJsonPrettyStr(form())).append(StrUtil.CRLF);
		}
		sb.append(super.toString());
		String string = sb.toString();
		if (logHeader){
			return string;
		}
		String before = StrUtil.subBefore(string, "Request Headers: ", false) + "Request Body: ";
		String after = StrUtil.subAfter(string, "Request Body: ", false);
		if (JSONUtil.isJson(after)) {
			after = JSONUtil.toJsonPrettyStr(after);
		}
		return before + after;
	}

	public void setLogHeader(boolean logHeader){
		this.logHeader = logHeader;
	}
}
