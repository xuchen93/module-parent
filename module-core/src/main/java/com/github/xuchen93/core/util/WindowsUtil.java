package com.github.xuchen93.core.util;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;

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
}
