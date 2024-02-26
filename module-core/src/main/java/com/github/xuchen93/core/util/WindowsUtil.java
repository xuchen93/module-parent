package com.github.xuchen93.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuchen.wang
 * @date 2024/1/3
 */
public class WindowsUtil {

    /**
     * 替换文件目录中的非法字符
     * @param dir
     * @param to
     * @return
     */
    public static String makeDirLegality(String dir,String to) {
        ArrayList<String> list = CollUtil.newArrayList("|", "/", "\\", "*", ">", "<", "?", ".", "^");
        for (String s : list) {
            dir = dir.replace(s, to);
        }
        return dir;
    }

    /**
     * 将chrome浏览器复制下来的header解析成map结构
     * @param copyHeader
     * @return
     */
    public static Map<String,String> resolveChromeRequestHeader(String copyHeader){
        String[] split = copyHeader.split("\n");
        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < split.length; i++) {
            if (split[i].startsWith(":")){
                i=i+1;
                continue;
            }
            map.put(StrUtil.subPre(split[i],split[i].length()-1),split[++i]);
        }
        return map;
    }
}
