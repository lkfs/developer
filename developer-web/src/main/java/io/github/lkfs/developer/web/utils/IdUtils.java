package io.github.lkfs.developer.web.utils;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdUtils extends IdUtil {

    private static Long workerId;

    @Value("${dp.snowflake.workId:1}")
    public void setWorkerId(Long workerIdArg) {
        workerId = workerIdArg;
    }

    public static String nextSnowflakeId() {
        long id = IdUtil.getSnowflake(workerId).nextId();
        return "" + id;
    }

}
