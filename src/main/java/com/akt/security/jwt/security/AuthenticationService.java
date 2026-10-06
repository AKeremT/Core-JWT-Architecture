package com.akt.security.jwt.security;

import com.akt.security.jwt.dto.AuthResponse;
import com.akt.security.jwt.dto.LoginRequest;
import com.akt.security.jwt.dto.RegisterRequest;
import com.akt.security.jwt.exception.UserAlreadyExistsException;
import com.akt.security.jwt.model.CustomUserDetails;
import com.akt.security.jwt.model.Role;
import com.akt.security.jwt.model.User;
import com.akt.security.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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

        final String token = jwtService.generateToken(new CustomUserDetails(savedUser));
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest loginRequest) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();

        final String token = jwtService.generateToken(customUserDetails);
        return new AuthResponse(token);
    }

}
