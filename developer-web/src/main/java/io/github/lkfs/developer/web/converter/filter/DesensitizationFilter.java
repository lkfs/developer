package io.github.lkfs.developer.web.converter.filter;

import com.alibaba.fastjson2.filter.ValueFilter;
import io.github.lkfs.developer.web.annotation.Desensitization;
import io.github.lkfs.developer.web.annotation.DesensitizationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;

/**
 * 脱敏注解过滤器
 *
 * @author : Luis
 * @date : 2021/4/15 17:26
 */
@Slf4j
public class DesensitizationFilter implements ValueFilter {
    @Override
    public Object apply(Object object, String name, Object value) {

        if (ObjectUtils.isEmpty(value) || !(value instanceof String)) {
            return value;
        }
        Field field;
        try {
            field = object.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            try {
                field = object.getClass().getSuperclass().getDeclaredField(name);
            } catch (Exception noSuchFieldException) {
                return value;
            }
        }

        Desensitization desensitization;
        if (field == null || String.class != field.getType() || (desensitization = field.getAnnotation(Desensitization.class)) == null) {
            return value;
        }
        String valueStr = (String) value;

        DesensitizationType type = desensitization.type();
        if (type.equals(DesensitizationType.ID_CARD)) {
            value = this.hiddenIdCard(valueStr);
        }
        if (type.equals(DesensitizationType.PHONE)) {
            value = this.hiddenPhone(valueStr);
        }
        return value;
    }

    private String hiddenPhone(String phone) {
        if (ObjectUtils.isEmpty(phone)) {
            return "";
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    private String hiddenIdCard(String idCard) {
        if (ObjectUtils.isEmpty(idCard)) {
            return "";
        }
        return idCard.replaceAll("(\\w{4})(\\w{2})(\\w*)(\\w{6})", "$1**$3******");
    }

}
