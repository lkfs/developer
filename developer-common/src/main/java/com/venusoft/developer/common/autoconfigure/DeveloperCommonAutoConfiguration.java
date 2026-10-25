package com.venusoft.developer.common.autoconfigure;

import com.venusoft.developer.common.security.AesUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({AesUtils.class})
public class DeveloperCommonAutoConfiguration {
}
