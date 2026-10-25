package com.venusoft.developer.common.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : Luis
 * @date : 2021/4/2 11:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends RuntimeException {

    private int code = 500;
    private String message;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException withMessage(String message) {
        return new BizException(this.code, message);
    }

    public BizException withMessage(String message, Object... params) {
        return new BizException(this.code, StrUtil.format(message, params));
    }

    /**
     * 业务异常
     */
    public static final BizException BUSINESS_FAULT = new BizException(501, "business fault");
    /**
     * 请求参数错误
     */
    public static final BizException PARAMS_ERROR = new BizException(502, "params error");
    /**
     * 数据未找到
     */
    public static final BizException DATA_NOT_FOUND = new BizException(511, "data not found");
    /**
     * 数据已存在
     */
    public static final BizException DATA_ALREADY_EXISTED = new BizException(512, "data already existed");
    /**
     * 数据访问异常
     */
    public static final BizException DATA_ACCESS_ERROR = new BizException(513, "data access error");
    /**
     * 数据更新异常
     */
    public static final BizException DATA_UPDATE_ERROR = new BizException(514, "data update error");
    /**
     * 接口异常
     */
    public static final BizException INTERFACE_ERROR = new BizException(521, "interface error");
    /**
     * 接口禁止访问
     */
    public static final BizException INTERFACE_FORBIDDEN = new BizException(522, "interface forbidden");


    /*public static final BizException HAVE_NO_ROLE_FORBIDDEN = new BizException(5001, "没有可执行该操作的角色");
    public static final BizException USER_NOT_EXIST = new BizException(5002, "用户不存在");
    public static final BizException LOGIN_EXPIRED = new BizException(5003, "登录过期");
    public static final BizException ORDER_NOT_EXIST = new BizException(5004, "订单不存在");
    public static final BizException INSUFFICIENT_BALANCE = new BizException(5005, "余额不足");
    public static final BizException USER_PASSWORD_ERROR = new BizException(5006, "账号或密码错误");
    public static final BizException USER_FORBID_ERROR = new BizException(5007, "账户已禁用");*/


}
