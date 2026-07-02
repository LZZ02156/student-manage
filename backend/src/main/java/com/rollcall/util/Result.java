package com.rollcall.util;

import java.util.HashMap;
import java.util.Map;

public class Result {
    private int code;
    private String msg;
    private Object data;

    public static Result success(String msg, Object data) {
        Result r = new Result();
        r.code = 200;
        r.msg = msg;
        r.data = data;
        return r;
    }
    public static Result success(String msg) { return success(msg, null); }
    public static Result error(String msg) {
        Result r = new Result();
        r.code = 500;
        r.msg = msg;
        return r;
    }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public Object getData() { return data; }

    public void setCode(int code) { this.code = code; }
    public void setMsg(String msg) { this.msg = msg; }
    public void setData(Object data) { this.data = data; }
}
