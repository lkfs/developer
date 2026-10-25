package io.github.lkfs.developer.redis.configuration;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * @author luisl
 */
@Slf4j
public class FastJson2RedisSerializer<T> implements RedisSerializer<T> {

    private static final JSONWriter.Feature[] writerFeatures = new JSONWriter.Feature[]{
            JSONWriter.Feature.WriteClassName,
            JSONWriter.Feature.FieldBased,
            JSONWriter.Feature.ReferenceDetection,
            JSONWriter.Feature.NotWriteDefaultValue,
            JSONWriter.Feature.WriteNameAsSymbol,
            JSONWriter.Feature.WriteEnumsUsingName
    };
    private static final JSONReader.Feature[] readerFeatures = new JSONReader.Feature[]{
            JSONReader.Feature.UseDefaultConstructorAsPossible,
            JSONReader.Feature.UseNativeObject,
            JSONReader.Feature.FieldBased,
            JSONReader.Feature.ErrorOnNotSupportAutoType
    };

    private final Class<T> clazz;
    private final JSONReader.AutoTypeBeforeHandler autoTypeFilter;

    public FastJson2RedisSerializer(Class<T> clazz, String... autoTypePackages) {
        super();
        this.clazz = clazz;
        // 按需加入需要支持自动类型的类名前缀，范围越小越安全
        this.autoTypeFilter = JSONReader.autoTypeFilter(autoTypePackages);
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (Objects.isNull(t)) {
            return new byte[0];
        }
        try {
            return JSON.toJSONBytes(t, writerFeatures);
        } catch (Exception e) {
            log.error("Fastjson2 序列化错误：{}", e.getMessage());
            throw new SerializationException("无法序列化: " + e.getMessage(), e);
        }

    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (ObjectUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            return JSON.parseObject(bytes, clazz, autoTypeFilter, readerFeatures);
        } catch (Exception e) {
            log.error("Fastjson2 反序列化错误：{}", e.getMessage());
            throw new SerializationException("无法反序列化: " + e.getMessage(), e);
        }

    }
}
