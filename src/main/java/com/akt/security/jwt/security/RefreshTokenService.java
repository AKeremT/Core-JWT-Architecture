package com.akt.security.jwt.security;

import com.akt.security.jwt.config.JwtProperties;
import com.akt.security.jwt.exception.TokenNotFoundException;
import com.akt.security.jwt.model.RefreshToken;
import com.akt.security.jwt.model.User;
import com.akt.security.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshToken getRefreshToken(User user) {

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.getRefreshTokenExpiration());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(expiration)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {

            refreshTokenRepository.delete(refreshToken);
            throw new JwtException("Refresh token expired");
        }

        return refreshToken;
    }

    public Optional<RefreshToken> findByToken(String token) {

        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }
        return refreshTokenRepository.findByToken(token);
    }
}
