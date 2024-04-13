package com.mokhir.dev.BookShop.jwt;

import com.mokhir.dev.BookShop.aggregation.dto.users.SignInResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.aggregation.mapper.RoleMapper;
import com.mokhir.dev.BookShop.repository.interfaces.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;


    @Value("${jwt.token.secret}")
    private String secret;
    @Value("${jwt.token.validity}")
    private long validTime;


    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validate(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            Date expirationDate = claims.getExpiration();
            Date now = new Date();
            return now.after(expirationDate);
        } catch (Exception ex) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUser(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUser(String token) {
        Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return body.getSubject();
    }

    public SignInResponse createToken(User user, boolean rememberMe) {
        SignInResponse signInResponse = new SignInResponse();
        signInResponse.setId(user.getId());
        signInResponse.setToken(generateToken(user));
        signInResponse.setUsername(user.getUsername());
        signInResponse.setFirstName(user.getFirstName());
        signInResponse.setLastName(user.getLastName());
        signInResponse.setRole(roleMapper.toDto(user.getRole()));
        if (rememberMe){
            user.setRefreshToken(generateRefreshToken(user));
            signInResponse.setRefreshToken(user.getRefreshToken());
        }else {
            user.setRefreshToken(null);
        }
        userRepository.save(user);
        return signInResponse;
    }

    private String generateRefreshToken(User user) {
        return Base64.getEncoder().encodeToString(generateToken(user).getBytes());
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        Instant validity = now.plusMillis(validTime);
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("role", user.getRole());
        String compact = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(validity))
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS256, secret).compact();
        return compact;
    }
}
