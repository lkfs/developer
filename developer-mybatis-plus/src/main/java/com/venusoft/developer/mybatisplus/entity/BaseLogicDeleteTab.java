package com.venusoft.developer.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.spring.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MyBatis Plus 审计字段 + 逻辑删除基类：通过 deleted_flag 软删除
 *
 * @param <T>  实体类
 * @param <ID> 用户主键类型
 * @author : lkfs
 */

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseLogicDeleteTab<T extends Model<T>, ID> extends BaseAuditTab<T, ID> {

    /**
     * 删除标记
     */
    @TableLogic(value = "0", delval = "1")
    protected Boolean deletedFlag = false;

}
