package com.rollcall.util;

public class BaseResponse<T> {
    private int code;
    private String msg;
    private T data;

    public boolean isSuccess() { return code == 200; }
    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public T getData() { return data; }
    public void setCode(int code) { this.code = code; }
    public void setMsg(String msg) { this.msg = msg; }
    public void setData(T data) { this.data = data; }
}
