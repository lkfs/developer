package io.github.lkfs.developer.crypto;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
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
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 通信 body 加解密
 *
 * @author : Luis
 * @date : 2021/4/8 9:14
 */
@Slf4j
@ControllerAdvice
@Order(99)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CryptoResponseBodyAdvice implements ResponseBodyAdvice {
    private static final String logTemplate = "[RESPONSE], time : {}, Body : {}";
    @Autowired
    private CryptoConfiguration cryptoConfiguration;
    @Autowired
    private AesUtils aesUtils;
    @Autowired
    private FastJsonConfig fastJsonConfig;

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }


    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        Long timeSpan = RequestTimeHolder.getTimeSpan();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        boolean finalEnabled = cryptoConfiguration.finalEnabled(uri);
        if (finalEnabled) {
            /**
             * 加密处理，由于ResponseBodyAdvice早于HttpMessageConverters中的filter执行，导致vo中的注解没来得及处理
             * 所以，加密前先执行json转化
             */
            String json = JSON.toJSONString(body,
                    fastJsonConfig.getDateFormat(),
                    fastJsonConfig.getWriterFilters(),
                    fastJsonConfig.getWriterFeatures());

            String cryptoText = aesUtils.encrypt(json);
            JSONObject result = new JSONObject();
            result.put("data", cryptoText);
            log.info(logTemplate, timeSpan, cut(LogMaskUtils.mask(json)));
            return result;
        } else {
            log.info(logTemplate, timeSpan, cut(LogMaskUtils.mask(JSON.toJSONString(body))));
            return body;
        }
    }

    private String cut(String json) {
        if (json.length() > 500) {
            return json.substring(0, 300) + "...";
        }
        return json;
    }
}
