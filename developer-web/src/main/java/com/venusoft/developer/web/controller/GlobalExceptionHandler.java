package com.venusoft.developer.web.controller;

import com.venusoft.developer.common.exception.BizException;
import com.venusoft.developer.common.result.CommonResult;
import com.venusoft.developer.web.language.IErrorLanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Luis
 * @date : 2021/4/2 11:47
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Pattern messageCodePattern = Pattern.compile("^(\\d+):");

    @Autowired(required = false)
    private IErrorLanguageService errorLanguageService;

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public CommonResult<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据更新异常，message:", e);
        return new CommonResult<>(BizException.DATA_UPDATE_ERROR.getCode(), BizException.DATA_UPDATE_ERROR.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    public CommonResult<String> handleDataAccessException(DataAccessException e) {
        log.error("数据访问异常，message:", e);
        return new CommonResult<>(BizException.DATA_ACCESS_ERROR.getCode(), BizException.DATA_ACCESS_ERROR.getMessage());
    }

    /**
     * 接口参数Validated校验异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    protected CommonResult<String> handleBindException(BindException e) {
        String messageCode = e.getBindingResult().getFieldError().getDefaultMessage();
        Matcher matcher = messageCodePattern.matcher(messageCode);
        if (errorLanguageService != null && matcher.find()) {
            //多语言处理
            messageCode = matcher.group(1);
            String message = errorLanguageService.getMessage(messageCode);
            if (message != null) {
                int code = Integer.parseInt(messageCode);
                return new CommonResult<>(code, message, null);
            }
        }

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(o -> o.getDefaultMessage())
                .findFirst()
                .orElse("BindException:unknow");
        return new CommonResult<String>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    /**
     * 接口参数嵌套对象的@Valid校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    protected CommonResult<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String messageCode = e.getBindingResult().getFieldError().getDefaultMessage();
        Matcher matcher = messageCodePattern.matcher(messageCode);
        if (errorLanguageService != null && matcher.find()) {
            //多语言处理
            messageCode = matcher.group(1);
            String message = errorLanguageService.getMessage(messageCode);
            if (message != null) {
                return new CommonResult<>(Integer.parseInt(messageCode), message);
            }
        }

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(o -> o.getField() + ":" + o.getDefaultMessage())
                .findFirst()
                .orElse("BindException:unknow");
        return new CommonResult<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    /**
     * 业务异常的处理器
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public CommonResult<String> handleBizException(BizException e) {
        if (errorLanguageService != null) {
            //多语言处理
            Integer code = e.getCode();
            String message = errorLanguageService.getMessage(String.valueOf(code));
            if (message != null) {
                return new CommonResult<>(code, message);
            }
        }
        return new CommonResult<>(e.getCode(), e.getMessage());
    }

    /**
     * Total异常的处理器
     *
     * @param e
     * @return java.lang.NullPointerException
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public CommonResult<String> handleException(NullPointerException e) {
        log.error("空指针异常", e);
        return new CommonResult<String>(9988, "NullPointerException：" + e.getMessage(), null);
    }

    /**
     * Total异常的处理器
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public CommonResult<String> handleException(RuntimeException e) {
        log.error("运行时异常", e);
        return new CommonResult<String>(9999, "RuntimeException" + e.getMessage(), null);
    }


}
