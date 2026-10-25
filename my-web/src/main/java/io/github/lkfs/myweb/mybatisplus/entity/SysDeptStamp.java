package io.github.lkfs.myweb.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lkfs.developer.mybatisplus.entity.BaseAuditTab;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dept_stamp")
public class SysDeptStamp extends BaseAuditTab<SysDeptStamp, String> {
    private Long id;
    private Long deptId;
    private String stampName;
    private String stampImgUrl;
    private Integer status;
    private String createBy;
    private String updateBy;
    private String createTime;
    private String updateTime;
    private String delFlag;
}

