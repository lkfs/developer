package io.github.lkfs.developer.web.converter.filter;


import com.alibaba.fastjson2.filter.ValueFilter;
import io.github.lkfs.developer.obs.annotation.BucketType;
import io.github.lkfs.developer.obs.annotation.TencentCos;
import io.github.lkfs.developer.obs.service.TencentCosService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 腾讯云对象存储注解过滤器
 *
 * @author : Luis
 * @date : 2021/4/15 17:26
 */
@Slf4j
public class TencentCosFilter implements ValueFilter {

    private TencentCosService tencentCosService;

    public TencentCosFilter(TencentCosService tencentCosService) {
        this.tencentCosService = tencentCosService;
    }

    @Override
    public Object apply(Object object, String name, Object value) {

        if (null == value || !(value instanceof String) || ((String) value).length() == 0) {
            return value;
        }
        try {
            Field field = object.getClass().getDeclaredField(name);

            TencentCos tencentCos = null;
            if (String.class != field.getType() || (tencentCos = field.getAnnotation(TencentCos.class)) == null) {
                return value;
            }
            String valueStr = (String) value;

            BucketType type = tencentCos.type();
            if (type.equals(BucketType.PRIVATE)) {
                value = this.tencentCosService.getPrivateUrl(valueStr);
            }

            if (type.equals(BucketType.PUBLIC)) {
                value = this.tencentCosService.getPublicUrl(valueStr);
            }
        } catch (NoSuchFieldException e) {
            return value;
        }
        return value;
    }
}
