package io.github.lkfs.developer.common.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * @author : Luis
 * @date : 2021/4/14 19:08
 */
public interface IBaseUser<ID> extends UserDetails {
    ID getUserId();

    default List<String> getRoleSet() {
        return List.of();
    }

    default List<String> getPermissionSet() {
        return List.of();
    }
}
