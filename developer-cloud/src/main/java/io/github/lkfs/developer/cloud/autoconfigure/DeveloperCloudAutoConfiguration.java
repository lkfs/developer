package io.github.lkfs.developer.cloud.autoconfigure;

import io.github.lkfs.developer.cloud.feign.configuration.FeignSupportConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(FeignSupportConfig.class)
public class DeveloperCloudAutoConfiguration {
}
