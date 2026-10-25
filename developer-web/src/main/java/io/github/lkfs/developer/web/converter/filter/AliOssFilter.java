package io.github.lkfs.developer.web.converter.filter;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.filter.AfterFilter;
import io.github.lkfs.developer.obs.annotation.AliOss;
import io.github.lkfs.developer.obs.annotation.BucketType;
import io.github.lkfs.developer.obs.service.AliOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;

/**
 * 阿里云对象存储注解过滤器
 *
 * @author : Luis
 * @date : 2021/4/15 17:26
 */
@Slf4j
public class AliOssFilter extends AfterFilter {
    private AliOssService aliOssService;

    public AliOssFilter(AliOssService aliOssService) {
        this.aliOssService = aliOssService;
    }

    @Override
    public void writeAfter(Object object) {
        Field[] fields = ReflectUtil.getFields(object.getClass());
        for (Field field : fields) {
            AliOss aliOSS = field.getAnnotation(AliOss.class);
            if (aliOSS != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (!ObjectUtils.isEmpty(value)) {
                        BucketType type = aliOSS.type();
                        if (type.equals(BucketType.PUBLIC)) {
                            value = this.aliOssService.getPublicUrl(value.toString());
                        }
                        if (type.equals(BucketType.PRIVATE)) {
                            value = this.aliOssService.getPrivateUrl(value.toString());
                        }
                        super.writeKeyValue(field.getName() + "View", value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
