package com.venusoft.developer.common.autoconfigure;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * 全工程唯一的 fastjson 序列化配置。
 * 各模块通过定义 {@link Filter} Bean 贡献自己的过滤器。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter")
public class DeveloperJsonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FastJsonConfig fastJsonConfigLocal(ObjectProvider<Filter> filters) {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setWriterFilters(filters.orderedStream().toArray(Filter[]::new));
        fastJsonConfig.setDateFormat("millis");
        fastJsonConfig.setReaderFeatures(
                JSONReader.Feature.FieldBased,
                JSONReader.Feature.SupportArrayToBean);
        fastJsonConfig.setWriterFeatures(
                JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.WriteNullListAsEmpty,
                JSONWriter.Feature.WriteNulls,
                JSONWriter.Feature.WriteNullBooleanAsFalse);
        return fastJsonConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerHttpMessageConvertersCustomizer fastJsonHttpMessageConverters(FastJsonConfig fastJsonConfig) {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        fastConverter.setFastJsonConfig(fastJsonConfig);
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.valueOf("application/*+json"));
        supportedMediaTypes.add(MediaType.ALL);
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);
        return converters -> converters.withJsonConverter(fastConverter);
    }
}
