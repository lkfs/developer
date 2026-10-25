package com.venusoft.developer.mongo;

import lombok.Data;

import java.util.List;

/**
 * 通用设置排序QO对象
 *
 * @author : QRQY
 * @date : 2021-04-29 10:37
 */
@Data
public class BaseMongoSetSortQO<ID> {

    /**
     * entityId
     */
    private ID id;
    /**
     * 排序
     */
    private Integer sort;

    /**
     * entityIds
     */
    private List<ID> ids;
}
