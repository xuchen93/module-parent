package pers.xuchen.module.web.common;

import cn.hutool.extra.spring.SpringUtil;

public class RequestContextProxy {

    public static RequestContextBean getRequestContextBean() {
        return SpringUtil.getBean(RequestContextBean.class);
    }

    public static String getUserId() {
        return getRequestContextBean().getUserId();
    }

    public static String getUserName() {
        return getRequestContextBean().getUserName();
    }

    public static String getNickNamwe() {
        return getRequestContextBean().getNickName();
    }
}
