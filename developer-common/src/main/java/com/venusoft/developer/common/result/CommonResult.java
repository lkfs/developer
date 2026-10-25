package com.venusoft.developer.common.result;

import lombok.Data;

/**
 * @author : Luis
 * @date : 2021/4/1 11:27
 */
@Data
public class CommonResult<T> implements ICommonResult {

    private int code;
    private String message;
    private T result;

    public CommonResult() {
        this.code = 200;
        this.message = "success";
    }

    public CommonResult(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public CommonResult(T result) {
        this();
        this.result = result;
    }

    public CommonResult(int code, String msg, T result) {
        this.code = code;
        this.message = msg;
        this.result = result;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
