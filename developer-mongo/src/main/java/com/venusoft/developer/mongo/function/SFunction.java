package com.venusoft.developer.mongo.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的函数接口
 *
 * @author luis
 * @date 2024/7/8 13:14
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
