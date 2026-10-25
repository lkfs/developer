package io.github.lkfs.developer.web.configuration;

import cn.hutool.core.util.IdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * LogMdcInterceptor
 * refer: https://mp.weixin.qq.com/s/o6sNiYNCmLCr-mSMjLKgBg
 *
 * @author : lkfs
 * @date : 2024/2/3 18:36
 */
public class LogMdcInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tid = null;
        //可以考虑让客户端传入链路ID，但需保证一定的复杂度唯一性；如果没使用则自动生成
        String traceId = request.getHeader(TRACE_ID);
        if (!ObjectUtils.isEmpty(traceId)) {
            tid = traceId;
        } else {
            tid = IdUtil.fastSimpleUUID();
        }
        MDC.put(TRACE_ID, tid);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(TRACE_ID);
    }

}

