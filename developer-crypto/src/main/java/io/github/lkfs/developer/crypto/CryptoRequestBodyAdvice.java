package io.github.lkfs.developer.crypto;

import com.alibaba.fastjson2.JSONObject;
import io.github.lkfs.developer.common.log.LogMaskUtils;
import io.github.lkfs.developer.common.security.AesUtils;
import io.github.lkfs.developer.common.web.RequestTimeHolder;
import io.github.lkfs.developer.crypto.configuration.CryptoConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ObjectUtils;
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
@Order(2)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CryptoRequestBodyAdvice extends RequestBodyAdviceAdapter {
    @Autowired
    private CryptoConfiguration cryptoConfiguration;
    @Autowired
    private AesUtils aesUtils;

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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();

        RequestTimeHolder.init();

        JSONObject jsonObject;
        if (body instanceof JSONObject) {
            jsonObject = (JSONObject) body;
        } else {
            jsonObject = JSONObject.parseObject(JSONObject.toJSONString(body));
        }


        boolean finalEnabled = cryptoConfiguration.finalEnabled(uri);
        if (!finalEnabled) {
            if (!ObjectUtils.isEmpty(jsonObject.getString("data"))) {
                finalEnabled = true;
            }
        }


        if (finalEnabled) {
            //强制加密
            String encryptBody = jsonObject.getString("data");
            if (ObjectUtils.isEmpty(encryptBody)) {
                throw new HttpMessageConversionException("The data in request body is empty.");
            }

            String decryptBody = aesUtils.decrypt(encryptBody);
            if (ObjectUtils.isEmpty(decryptBody)) {
                throw new HttpMessageConversionException("The data in request body is error.");
            }

            JSONObject newJsonObject = JSONObject.parseObject(decryptBody);
            log.info(logTemplate, uri, LogMaskUtils.mask(JSONObject.toJSONString(newJsonObject)));
            return newJsonObject;
        } else {
            log.info(logTemplate, uri, LogMaskUtils.mask(JSONObject.toJSONString(jsonObject)));
            return body;
        }
    }
}