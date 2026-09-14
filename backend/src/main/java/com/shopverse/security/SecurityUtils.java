package com.shopverse.security;

import com.shopverse.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new UnauthorizedException("Authentication required");
        }
        return user;
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
