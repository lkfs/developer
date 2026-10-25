package com.venusoft.developer.mybatisplus.autoconfigure;

import com.venusoft.developer.mybatisplus.configuration.MyMetaObjectHandler;
import com.venusoft.developer.mybatisplus.configuration.MybatisPlusConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({MybatisPlusConfig.class, MyMetaObjectHandler.class})
public class DeveloperMybatisPlusAutoConfiguration {
}
