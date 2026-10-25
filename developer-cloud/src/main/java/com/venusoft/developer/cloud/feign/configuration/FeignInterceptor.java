package com.venusoft.developer.cloud.feign.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * feign 请求头拦截器
 *
 * @author lkfs
 */
@Slf4j
public class FeignInterceptor implements RequestInterceptor {

    private final Map<String, String> headerDefaultMap;
    private final Set<String> headerKeySet;

    public FeignInterceptor() {
        String[] headerKeys = {"platform", "version", "authorization", "content-type", "accept-language"};
        this.headerKeySet = new LinkedHashSet<>(Arrays.asList(headerKeys));
        this.headerDefaultMap = new LinkedHashMap<String, String>() {{
            put("platform", "WEB");
            put("version", "1.0");
            put("content-type", "application/json");
            put("accept-language", "en");
        }};
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, String> headers = getHeaders(getHttpServletRequest());
        for (String headerName : headers.keySet()) {
            requestTemplate.header(headerName, headers.get(headerName));
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes == null ? null : attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        if (request == null) {
            return new LinkedHashMap<>(headerDefaultMap);
        } else {
            Map<String, String> map = new LinkedHashMap<>();
            Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                if (needThisHeader(key)) {
                    String value = request.getHeader(key);
                    map.put(key, value);
                }
            }
            return map;
        }
    }


    private boolean needThisHeader(String headerName) {
        //todo:这里写你的逻辑，哪些header需要传递，千万不能把所有的header传下去
        return headerName != null && headerKeySet.contains(headerName.toLowerCase(Locale.ROOT));

    }
}