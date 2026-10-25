package com.venusoft.developer.mymongo.vo;

import com.venusoft.developer.common.enums.BaseEnum;

import java.util.Arrays;

/**
 * 前端平台枚举
 */
public enum TypeEnum implements BaseEnum {

    IOS("苹果"),
    ANDROID("安卓"),
    WEB("浏览器");

    private String message;

    TypeEnum(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public static TypeEnum parse(String name) {
        return Arrays.stream(TypeEnum.values())
                .filter(t -> t.name().equals(name))
                .findAny()
                .orElse(null);
    }
}
