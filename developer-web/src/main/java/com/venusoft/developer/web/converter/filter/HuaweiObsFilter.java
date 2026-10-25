package com.venusoft.developer.web.converter.filter;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.filter.AfterFilter;
import com.venusoft.developer.obs.annotation.BucketType;
import com.venusoft.developer.obs.annotation.HuaweiObs;
import com.venusoft.developer.obs.service.HuaweiObsService;
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
public class HuaweiObsFilter extends AfterFilter {
    private HuaweiObsService huaweiObsService;

    public HuaweiObsFilter(HuaweiObsService huaweiObsService) {
        this.huaweiObsService = huaweiObsService;
    }

    @Override
    public void writeAfter(Object object) {
        Field[] fields = ReflectUtil.getFields(object.getClass());
        for (Field field : fields) {
            HuaweiObs huaweiObs = field.getAnnotation(HuaweiObs.class);
            if (huaweiObs != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (!ObjectUtils.isEmpty(value)) {
                        BucketType type = huaweiObs.type();
                        if (type.equals(BucketType.PUBLIC)) {
                            value = this.huaweiObsService.getPublicUrl(value.toString());
                        }
                        if (type.equals(BucketType.PRIVATE)) {
                            value = this.huaweiObsService.getPrivateUrl(value.toString());
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
