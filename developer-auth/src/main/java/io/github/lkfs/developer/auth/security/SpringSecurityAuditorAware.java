package io.github.lkfs.developer.auth.security;

import io.github.lkfs.developer.common.security.IBaseUser;
import io.github.lkfs.developer.common.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 审计，通过Security提取用户，并保存到数据表的审计字段中
 *
 * @author : Luis
 * @date : 2021/4/13 16:46
 */
@Component
@Slf4j
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        IBaseUser<String> user = SecurityUtils.getUser(String.class);
        return user == null ? Optional.empty() : Optional.ofNullable(user.getUserId());
    }
}
