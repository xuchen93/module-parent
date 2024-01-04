package pers.xuchen.module.core.model;

import lombok.Data;

@Data
public class R {
    private boolean success;

    private int code;

    private String msg;

    private int count;

    private Object data;


    public static R success() {
        return success("操作成功");
    }

    public static R success(String msg) {
        return success(msg,null);
    }

    public static R success(Object data) {
        return success("操作成功",data);
    }

    public static R success(String msg, Object data) {
        return new R(true,0,msg,data);
    }

    public static R fail(String msg) {
        return fail(1,msg);
    }

    public static R fail(int code, String msg) {
        return new R(false, code, msg, null);
    }

    public static R fail() {
        return fail("操作失败");
    }


    private R(boolean success, int code, String msg, Object data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
