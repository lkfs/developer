package io.github.lkfs.developer.auth.autoconfigure;

import io.github.lkfs.developer.auth.aspect.PreAuthorizeAspect;
import io.github.lkfs.developer.auth.security.SpringSecurityAuditorAware;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({PreAuthorizeAspect.class, SpringSecurityAuditorAware.class})
public class DeveloperAuthAutoConfiguration {
}
