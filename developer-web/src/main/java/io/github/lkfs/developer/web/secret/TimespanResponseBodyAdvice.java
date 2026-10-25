package io.github.lkfs.developer.web.secret;

import com.alibaba.fastjson2.JSON;
import io.github.lkfs.developer.common.log.LogMaskUtils;
import io.github.lkfs.developer.common.web.RequestTimeHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 通信 body 加解密
 *
 * @author : Luis
 * @date : 2021/4/8 9:14
 */
@Slf4j
@ControllerAdvice
@Order(100)
@ConditionalOnMissingClass(value = {"io.github.lkfs.developer.crypto.CryptoResponseBodyAdvice"})
public class TimespanResponseBodyAdvice implements ResponseBodyAdvice {
    private static final String logTemplate = "[RESPONSE], time : {}, Body : {}";


    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }


    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        Long timeSpan = RequestTimeHolder.getTimeSpan();

        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            log.info(logTemplate, timeSpan, cut(LogMaskUtils.mask(JSON.toJSONString(body))));
        } else {
            log.info(logTemplate, timeSpan, body == null ? null : body.getClass().getName());
        }
        return body;
    }

    private String cut(String json) {
        if (json.length() > 1025) {
            return json.substring(0, 1024) + "...";
        }
        return json;
    }
}
