package io.github.lkfs.developer.mybatisplus.autoconfigure;

import io.github.lkfs.developer.mybatisplus.configuration.MyMetaObjectHandler;
import io.github.lkfs.developer.mybatisplus.configuration.MybatisPlusConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({MybatisPlusConfig.class, MyMetaObjectHandler.class})
public class DeveloperMybatisPlusAutoConfiguration {
}
