package pers.xuchen.module.web.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import pers.xuchen.module.core.config.XuchenProperties;
import pers.xuchen.module.web.common.RequestContextBean;
import pers.xuchen.module.web.jwt.JwtService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
public class RequestHandler implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        RequestContextBean requestContextBean = SpringUtil.getBean(RequestContextBean.class);
        XuchenProperties xuchenProperties = SpringUtil.getBean(XuchenProperties.class);
        JwtService jwtService = SpringUtil.getBean(JwtService.class);
        String token = request.getHeader(xuchenProperties.getJwt().getTokenKey());
        if (StrUtil.isBlank(token)){
            return false;
        }
        log.debug("请求头里拿到token：{}",token);
        JSONObject jsonObject = jwtService.parseToken(token);
        requestContextBean.setUserId(jsonObject.getStr(xuchenProperties.getJwt().getUserId()));
        requestContextBean.setUserName(jsonObject.getStr(xuchenProperties.getJwt().getUserName()));
        requestContextBean.setNickName(jsonObject.getStr(xuchenProperties.getJwt().getNickName()));
        requestContextBean.setTokenObj(jsonObject);
        return true;
    }
}
