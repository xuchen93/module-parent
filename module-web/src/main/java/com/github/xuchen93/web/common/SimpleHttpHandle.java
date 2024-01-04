package com.github.xuchen93.web.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.github.xuchen93.core.util.CommonUtil;
import com.github.xuchen93.web.common.model.PackHttpRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpCookie;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class SimpleHttpHandle {
	public String localUrl = "http://localhost:8080/";
	public String header = "Authorization";
	public String token = null;
	public boolean logHeader = false;
	public List<HttpCookie> cookieList = new LinkedList<>();
	public Map<String, String> headerMap = new LinkedHashMap<>();
	public PackHttpRequest request;

	public SimpleHttpHandle createRequest(String url,Method method) {
		buildRequest(url,method);
		return this;
	}

	public SimpleHttpHandle createGet(String url) {
		return createGet(url,null);
	}

	public SimpleHttpHandle createGet(String url, Object params) {
		request = buildRequest(url, Method.GET);
		if (params != null){
			request.body(HttpUtil.toParams(BeanUtil.beanToMap(params, false, true)));
		}
		return this;
	}

	public SimpleHttpHandle createPost(String url) {
		return createPost(url, null);
	}

	public SimpleHttpHandle createPost(String url, Object params) {
        request = buildRequest(url, Method.POST);
		if (params != null) {
			request.body(JSONUtil.toJsonStr(params));
		}
		return this;
	}

	public String executeGet(String url,Object params){
        createGet(url, params);
		return executeRequest();
	}

	public String executePost(String url,Object params){
        createPost(url, params);
		return executeRequest();
	}

	public String executeGetwithLog(String url,Object params){
		createGet(url, params);
		return executeRequestWithLog();
	}

	public String executePostwithLog(String url,Object params){
		createPost(url, params);
		return executeRequestWithLog();
	}


	public String executeRequestWithLog(){
		if (request == null) {
			throw new IllegalStateException("请先构造请求");
		}
		doBeforeExec();
		StopWatch watch = new StopWatch();
		watch.start();
		HttpResponse response = this.request.execute();
		watch.stop();
		CommonUtil.tryLogPrettyStr(response.body());
		log.info("耗时(ms)：" + watch.getLastTaskTimeMillis());
		return response.body();
	}

	public String executeRequest(){
		if (request == null) {
			throw new IllegalStateException("请先构造请求");
		}
		doBeforeExec();
		HttpResponse response = this.request.execute();
		return response.body();
	}

	public void doBeforeExec() {
		setToken(request);
		setCookie(request);
		setHeader(request);
		setLogHeader(logHeader);
	}

	private PackHttpRequest buildRequest(String url,Method method){
		if (url.startsWith("/")) {
			url = url.substring(1);
		}
		PackHttpRequest request = (PackHttpRequest) new PackHttpRequest(localUrl + url).method(method);
		this.request = request;
		return request;
	}

	private void setCookie(PackHttpRequest request) {
		if (cookieList.size() > 0) {
			request.cookie(ArrayUtil.toArray(cookieList, HttpCookie.class));
		}
	}

	private void setToken(PackHttpRequest request) {
		if (token != null) {
			request.header(header, token);
		}
	}

	private void setHeader(PackHttpRequest request) {
		if (headerMap.size() > 0) {
			request.addHeaders(headerMap);
		}
	}

	public String hello() {
		return createGet("xuchen/hello").executeRequest();
	}
}
