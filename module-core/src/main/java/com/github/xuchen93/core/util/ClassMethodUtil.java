package com.github.xuchen93.core.util;

import cn.hutool.core.util.StrUtil;

/**
 * @author xuchen.wang
 * @date 2024/1/2
 */
public class ClassMethodUtil {

    private ClassMethodUtil(){}

    public static String getCurrentMethodName(int level){
        return Thread.currentThread().getStackTrace()[level].getMethodName();
    }

    public static String getCurrentClassName(int level){
        return Thread.currentThread().getStackTrace()[level].getClassName();
    }

    public static String getOriginClassName(String className){
        if (className == null){
            return null;
        }
        return StrUtil.split(className,"$$EnhancerBySpringCGLIB").get(0);
    }

    public static String getOriginMethodName(String methodName){
        String name = methodName.replace("lambda$", "");
        if (name.charAt(name.length()-2) == '$' ||name.charAt(name.length()-3) == '$'){
            int index = StrUtil.indexOf(name, '$', name.length() - 3);
            name = StrUtil.subPre(name,index);
        }
        return name;
    }
}
