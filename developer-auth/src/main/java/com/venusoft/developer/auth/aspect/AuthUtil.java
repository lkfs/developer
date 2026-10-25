package com.venusoft.developer.auth.aspect;

import com.venusoft.developer.auth.annotation.RequirePermissions;
import com.venusoft.developer.auth.annotation.RequireRoles;
import com.venusoft.developer.common.exception.BizException;
import com.venusoft.developer.common.security.IBaseUser;
import com.venusoft.developer.common.security.SecurityUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Token 权限验证工具类
 *
 * @author ruoyi
 */
public class AuthUtil {

    /**
     * 验证角色
     *
     * @param requireRoles 角色权限注解
     */
    public static void checkRole(RequireRoles requireRoles) {
        checkRole(requireRoles.value());
    }

    private static Boolean checkRole(String... requireRoles) {
        IBaseUser<String> curUser = SecurityUtils.getUser(String.class);
        if (curUser == null) {
            throw BizException.DATA_NOT_FOUND.withMessage("The user does not exists in check role");
        }
        List<String> myRoles = curUser.getRoleSet();
        if (!ObjectUtils.isEmpty(myRoles)) {
            for (String myRole : myRoles) {
                Boolean hasRole = Arrays.stream(requireRoles)
                        .filter(StringUtils::hasText)
                        .anyMatch(requireRole -> PatternMatchUtils.simpleMatch(requireRole, myRole));
                if (hasRole) {
                    return true;
                }
            }
        }
        throw BizException.INTERFACE_FORBIDDEN.withMessage("没有可执行该操作的权限！");
    }

    /**
     * 验证角色
     *
     * @param requirePermissions 角色权限注解
     */
    public static void checkPermissions(RequirePermissions requirePermissions) {
        checkPermissions(requirePermissions.value());
    }

    private static Boolean checkPermissions(String... requirePermissions) {
        IBaseUser<String> curUser = SecurityUtils.getUser(String.class);
        if (curUser == null) {
            throw BizException.DATA_NOT_FOUND.withMessage("The user does not exists in check role");
        }
        List<String> myPermissions = curUser.getPermissionSet();
        if (!ObjectUtils.isEmpty(myPermissions)) {
            for (String myPermission : myPermissions) {
                Boolean hasPermission = Arrays.stream(requirePermissions)
                        .filter(StringUtils::hasText)
                        .anyMatch(requireRole -> PatternMatchUtils.simpleMatch(requireRole, myPermission));
                if (hasPermission) {
                    return true;
                }
            }
        }
        throw BizException.INTERFACE_FORBIDDEN.withMessage("没有可执行该操作的权限！");
    }
}
