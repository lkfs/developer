package io.github.lkfs.developer.mongo;

import lombok.Data;

/**
 * 通用设置状态QO对象
 *
 * @author : QRQY
 * @date : 2021-04-29 10:37
 */
@Data
public class BaseMongoSetStatusQO<STATUS, ID> {

    /**
     * entityId
     */
    private ID id;
    /**
     * 状态
     */
    private STATUS status;
}
