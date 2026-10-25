package io.github.lkfs.developer.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.spring.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * MyBatis Plus 审计字段基类：填充创建/更新人与时间，不含逻辑删除
 *
 * @param <T>  实体类
 * @param <ID> 用户主键类型
 * @author : lkfs
 */

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAuditTab<T extends Model<T>, ID> extends Model<T> {

    /**
     * 创建用户
     */
    @TableField(fill = FieldFill.INSERT)
    protected ID createdBy;

    /**
     * 创建时间戳
     */
    @TableField(fill = FieldFill.INSERT)
    protected Date createdAt;

    /**
     * 更新用户
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected ID updatedBy;

    /**
     * 更新时间戳
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date updatedAt;

}
