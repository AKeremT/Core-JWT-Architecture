package com.akt.security.jwt.security;

import com.akt.security.jwt.dto.AuthResponse;
import com.akt.security.jwt.dto.LoginRequest;
import com.akt.security.jwt.dto.RefreshTokenRequest;
import com.akt.security.jwt.dto.RegisterRequest;
import com.akt.security.jwt.exception.TokenNotFoundException;
import com.akt.security.jwt.exception.UserAlreadyExistsException;
import com.akt.security.jwt.model.CustomUserDetails;
import com.akt.security.jwt.model.RefreshToken;
import com.akt.security.jwt.model.Role;
import com.akt.security.jwt.model.User;
import com.akt.security.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest registerRequest) {

        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .email(registerRequest.email())
                .roles(Set.of(Role.USER))
                .build();
        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(new CustomUserDetails(savedUser));
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(savedUser);
        return new AuthResponse(token, refreshToken.getToken());
    }

    public AuthResponse login(LoginRequest loginRequest) {

        var authUser = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        CustomUserDetails user = (CustomUserDetails) Objects.requireNonNull(authUser.getPrincipal());
        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(user.getUser());
        return new AuthResponse(token, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        return refreshTokenService.findByToken(refreshTokenRequest.token())
                .map(refreshTokenService::verifyRefreshToken)
                .map(RefreshToken::getUser)
                .map(user -> {
                    final String accessToken = jwtService.generateToken(new CustomUserDetails(user));
                    final RefreshToken newRefreshToken = refreshTokenService.getRefreshToken(user);
                    return new AuthResponse(accessToken, newRefreshToken.getToken());
                })
                .orElseThrow(TokenNotFoundException::new);
    }
}
