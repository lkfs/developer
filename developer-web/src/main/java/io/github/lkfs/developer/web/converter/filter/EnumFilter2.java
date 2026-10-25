package io.github.lkfs.developer.web.converter.filter;

import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.filter.AfterFilter;
import io.github.lkfs.developer.common.enums.BaseEnum;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;

/**
 * 枚举类型注解过滤器
 * 在返回json中同时展现key值和枚举的message值
 *
 * @author luis
 * @date 2024/7/2 9:05
 */
public class EnumFilter2 extends AfterFilter {

    @Override
    public void writeAfter(Object object) {
        Field[] fields = ReflectUtil.getFields(object.getClass(), new Filter<Field>() {
            @Override
            public boolean accept(Field field) {
                return BaseEnum.class.isAssignableFrom(field.getType());
            }
        });

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                if (!ObjectUtils.isEmpty(value)) {
                    if ((value instanceof BaseEnum)) {
                        //枚举对象序列化
                        BaseEnum baseEnum = (BaseEnum) value;
                        super.writeKeyValue(field.getName() + "View", baseEnum.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
