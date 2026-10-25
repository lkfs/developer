package com.venusoft.developer.mybatisplus.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.venusoft.developer.common.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        String userId = SecurityUtils.getUserId(String.class);
        Date curDate = new Date();
        this.strictInsertFill(metaObject, "createdBy", String.class, userId);
        this.strictInsertFill(metaObject, "createdAt", Date.class, curDate);
        this.strictInsertFill(metaObject, "updatedBy", String.class, userId);
        this.strictUpdateFill(metaObject, "updatedAt", Date.class, curDate);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updatedBy", String.class, SecurityUtils.getUserId(String.class));
        this.strictUpdateFill(metaObject, "updatedAt", Date.class, new Date());
    }
}
