package com.venusoft.developer.web.secret;

import com.alibaba.fastjson2.JSON;
import com.venusoft.developer.common.log.LogMaskUtils;
import com.venusoft.developer.common.web.RequestTimeHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 通信 body 加解密
 *
 * @author : Luis
 * @date : 2021/4/4 22:58
 */
@Slf4j
@ControllerAdvice
@Order(1)
@ConditionalOnMissingClass(value = {"com.venusoft.developer.crypto.CryptoRequestBodyAdvice"})
public class TimespanRequestBodyAdvice extends RequestBodyAdviceAdapter {


    private static final String logTemplate = "[REQUEST], path : {}, Body : {}";

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    /**
     * 对密文进行解密
     *
     * @param body
     * @param inputMessage
     * @param parameter
     * @param targetType
     * @param converterType
     * @return
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        RequestTimeHolder.init();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();

        log.info(logTemplate, uri, cut(LogMaskUtils.mask(JSON.toJSONString(body))));
        return body;

    }

    private String cut(String json) {
        if (json.length() > 1025) {
            return json.substring(0, 1024) + "...";
        }
        return json;
    }
}