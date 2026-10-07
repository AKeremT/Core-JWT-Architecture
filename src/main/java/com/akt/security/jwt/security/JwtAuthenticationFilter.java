package com.akt.security.jwt.security;

import com.akt.security.jwt.model.CustomUserDetails;
import com.akt.security.jwt.model.Role;
import com.akt.security.jwt.model.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authToken = authHeader.substring(7);

        var claimsOptional = jwtService.extractAllClaims(authToken);

        if (claimsOptional.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {

            Claims claims = claimsOptional.get();
            String username = claims.getSubject();
            List<String> roles = jwtService.extractRoles(claims);

            if (username != null) {

                Set<Role> userRoles = roles.stream()
                        .map(roleName -> roleName.startsWith("ROLE_") ? roleName.substring(5) : roleName)
                        .map(Role::valueOf)
                        .collect(Collectors.toSet());

                User user = User.builder()
                        .username(username)
                        .roles(userRoles)
                        .build();

                CustomUserDetails customUserDetails = new CustomUserDetails(user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                customUserDetails, null, customUserDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
