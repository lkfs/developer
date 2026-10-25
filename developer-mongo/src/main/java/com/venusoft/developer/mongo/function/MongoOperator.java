package com.venusoft.developer.mongo.function;

import java.util.Arrays;

/**
 * mongo 操作符
 *
 * @author lkfs
 */
public enum MongoOperator {
    Is,
    Eq,
    Neq,
    StartWith,
    EndWith,
    Like,
    Gt,
    Gte,
    Lt,
    Lte,
    Null,
    NotNull,
    Exists,
    Between,
    In,
    NotIn;

    public static MongoOperator parse(String name) {
        return Arrays.stream(MongoOperator.values())
                .filter(t -> t.name().equals(name))
                .findAny()
                .orElse(null);
    }
}
