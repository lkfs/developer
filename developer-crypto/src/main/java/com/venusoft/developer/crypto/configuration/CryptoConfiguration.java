package com.venusoft.developer.crypto.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;


/**
 * http通信加密配置
 *
 * @author lkfs
 */
@Component
@Data
@ConfigurationProperties(prefix = "crypto")
public class CryptoConfiguration {
    private Boolean enabled = false;
    private List<String> excludes = new ArrayList<>();

    /**
     * 是否使用安全请求
     *
     * @param path
     * @return
     */
    public boolean finalEnabled(String path) {
        if (!this.getEnabled()) {
            return false;
        }

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean noneExclude = this.getExcludes() == null
                              || this.getExcludes().stream().noneMatch(t -> antPathMatcher.match(t, path));
        return noneExclude;
    }

}
