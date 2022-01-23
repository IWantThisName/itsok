package com.itsok.bean;

public class ResultBean {

    private int code;

    private String message;

    private Object data;

    public static ResultBean ok() {
        return code(CodeEnum.OK);
    }

    public static ResultBean error() {
        return code(CodeEnum.ERROR);
    }

    public static ResultBean code(CodeEnum codeEnum) {
        return new ResultBean().setCode(codeEnum.getCode()).setMessage(codeEnum.getMessage());
    }

    public int getCode() {
        return code;
    }

    public ResultBean setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResultBean setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResultBean setData(Object data) {
        this.data = data;
        return this;
    }
}
