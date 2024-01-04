package com.github.xuchen93.web.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.github.xuchen93.core.config.XuchenProperties;
import com.github.xuchen93.model.ex.AuthException;
import com.github.xuchen93.web.common.RequestContextBean;
import com.github.xuchen93.web.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
public class RequestHandler implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		XuchenProperties xuchenProperties = SpringUtil.getBean(XuchenProperties.class);
		if (!xuchenProperties.getRequest().isCheckToken()) {
			return true;
		}
		RequestContextBean requestContextBean = SpringUtil.getBean(RequestContextBean.class);
		JwtService jwtService = SpringUtil.getBean(JwtService.class);
		String token = request.getHeader(xuchenProperties.getJwt().getTokenKey());
		if (StrUtil.isBlank(token)) {
			throw new AuthException(4002, "请求头缺少token");
		}
		log.debug("请求头里拿到token：{}", token);
		JSONObject jsonObject = jwtService.parseToken(token);
		requestContextBean.setUserId(jsonObject.getStr("id"));
		requestContextBean.setUserName(jsonObject.getStr("userName"));
		requestContextBean.setNickName(jsonObject.getStr("nickName"));
		requestContextBean.setTokenObj(jsonObject);
		return true;
	}
}
