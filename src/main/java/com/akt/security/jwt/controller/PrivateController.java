package com.akt.security.jwt.controller;

import com.akt.security.jwt.model.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
public class PrivateController {

    @GetMapping
    public String hello() {
        return "Hello from privateController";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user")
    public String getUser(@AuthenticationPrincipal CustomUserDetails user) {
        return "Hello " + user.getUsername() + ", your roles: " + user.getAuthorities();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String getAdmin(@AuthenticationPrincipal CustomUserDetails user) {
        return "Hello " + user.getUsername() + ", your roles: " + user.getAuthorities();
    }
}
