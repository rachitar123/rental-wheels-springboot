package com.rentwheels.config;

import com.rentwheels.entity.User;
import com.rentwheels.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private AuthService authService;

    @ModelAttribute("currentUserName")
    public String currentUserName(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        User user = authService.getUserByUsername(authentication.getName());
        return user != null ? user.getName() : authentication.getName();
    }
}
