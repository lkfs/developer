package io.github.lkfs.developer.common.log;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Locale;
import java.util.Map;

/**
 * 日志报文脱敏：对 password、phone、idCard 相关字段做掩码
 *
 * @author : lkfs
 */
public final class LogMaskUtils {

    private static final String MASK = "******";

    private LogMaskUtils() {
    }

    public static String mask(String json) {
        if (StrUtil.isBlank(json)) {
            return json;
        }
        try {
            Object node = JSONUtil.parse(json);
            maskNode(node);
            return JSONUtil.toJsonStr(node);
        } catch (Exception e) {
            // 无法解析时不输出原文，避免敏感信息泄露
            return MASK;
        }
    }

    private static void maskNode(Object node) {
        if (node instanceof JSONObject object) {
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                if (isSensitive(entry.getKey())) {
                    object.set(entry.getKey(), MASK);
                } else {
                    maskNode(entry.getValue());
                }
            }
        } else if (node instanceof JSONArray array) {
            for (int i = 0; i < array.size(); i++) {
                maskNode(array.get(i));
            }
        }
    }

    private static boolean isSensitive(String key) {
        String normalized = key.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return normalized.contains("password")
                || normalized.contains("phone")
                || normalized.contains("idcard");
    }
}
