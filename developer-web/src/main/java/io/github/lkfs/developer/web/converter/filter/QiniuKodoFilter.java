package io.github.lkfs.developer.web.converter.filter;

import com.alibaba.fastjson2.filter.ValueFilter;
import io.github.lkfs.developer.obs.annotation.BucketType;
import io.github.lkfs.developer.obs.annotation.QiniuKodo;
import io.github.lkfs.developer.obs.service.QiniuKodoService;

import java.lang.reflect.Field;

/**
 * 七牛云对象存储注解过滤器
 *
 * @author : Luis
 * @date : 2021/4/15 17:26
 */
public class QiniuKodoFilter implements ValueFilter {

    private QiniuKodoService qiniuKodoService;

    public QiniuKodoFilter(QiniuKodoService qiniuKodoService) {
        this.qiniuKodoService = qiniuKodoService;
    }

    @Override
    public Object apply(Object object, String name, Object value) {

        if (null == value || !(value instanceof String) || ((String) value).length() == 0) {
            return value;
        }
        try {
            Field field = object.getClass().getDeclaredField(name);

            QiniuKodo qiniuKodo = null;
            if (String.class != field.getType() || (qiniuKodo = field.getAnnotation(QiniuKodo.class)) == null) {
                return value;
            }
            String valueStr = (String) value;

            BucketType type = qiniuKodo.type();
            if (type.equals(BucketType.PRIVATE)) {
                value = this.qiniuKodoService.getPrivateUrl(valueStr);
            }

            if (type.equals(BucketType.PUBLIC)) {
                value = this.qiniuKodoService.getPublicUrl(valueStr);
            }
        } catch (NoSuchFieldException e) {
            return value;
        }
        return value;
    }
}
