package com.venusoft.developer.auth.autoconfigure;

import com.venusoft.developer.auth.aspect.PreAuthorizeAspect;
import com.venusoft.developer.auth.security.SpringSecurityAuditorAware;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({PreAuthorizeAspect.class, SpringSecurityAuditorAware.class})
public class DeveloperAuthAutoConfiguration {
}
