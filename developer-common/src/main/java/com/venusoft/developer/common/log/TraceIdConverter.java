package com.venusoft.developer.common.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

/**
 * @author : Luis
 * @date : 2021/4/19 9:52
 */
public class TraceIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return String.valueOf(MDC.get("traceId"));
    }
}