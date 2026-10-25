package com.venusoft.developer.common.web;

/**
 * @author : Luis
 * @date : 2021/4/29 13:49
 */
public class RequestTimeHolder {
    private final static ThreadLocal<Long> requestHolder = new ThreadLocal<>();

    public static void init() {
        requestHolder.set(System.currentTimeMillis());
    }

    public static Long getTimeSpan() {
        Long start = requestHolder.get();
        if (start != null) {
            Long timeSpan = System.currentTimeMillis() - requestHolder.get();
            requestHolder.remove();
            return timeSpan;
        } else {
            return -1L;
        }
    }
}
