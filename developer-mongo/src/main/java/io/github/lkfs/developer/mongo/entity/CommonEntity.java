package io.github.lkfs.developer.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

/**
 * mongo实体基类，提供审计字段和逻辑删除标记
 *
 * @author : Luis
 * @date : 2021/4/8 13:07
 */
@Data
public abstract class CommonEntity<STATUS, ID> {

    public STATUS getStatus() {
        throw new UnsupportedOperationException("Entity 的 getStatus 方法未实现");
    }

    public void setStatus(STATUS status) {
        throw new UnsupportedOperationException("Entity 的 setStatus 方法未实现");
    }

    /**
     * 创建用户
     */
    @CreatedBy
    protected ID createdBy;

    /**
     * 创建时间戳
     */
    @CreatedDate
    protected Date createdAt;

    /**
     * 更新用户
     */
    @LastModifiedBy
    protected ID updatedBy;

    /**
     * 更新时间戳
     */
    @LastModifiedDate
    protected Date updatedAt;

    /**
     * 删除标记
     */
    protected Boolean deletedFlag = false;

}
