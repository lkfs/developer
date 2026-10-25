package io.github.lkfs.developer.common.autoconfigure;

import io.github.lkfs.developer.common.security.AesUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({AesUtils.class})
public class DeveloperCommonAutoConfiguration {
}
