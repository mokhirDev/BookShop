package com.mokhir.dev.BookShop.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mokhir.dev.BookShop.aggregation.dto.users.SignInResponse;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.aggregation.mapper.RoleMapper;
import com.mokhir.dev.BookShop.repository.interfaces.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean validate(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
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
        String token = generateToken(user);
        signInResponse.setToken(token);
        signInResponse.setUsername(user.getUsername());
        signInResponse.setFirstName(user.getFirstName());
        signInResponse.setLastName(user.getLastName());
        signInResponse.setRole(user.getRole());
        if (rememberMe) {
            user.setRefreshToken(generateRefreshToken(user));
            signInResponse.setRefreshToken(user.getRefreshToken());
        } else {
            user.setRefreshToken(null);
        }
        userRepository.save(user);
        return signInResponse;
    }

    private String generateRefreshToken(User user) {
        return Base64.getEncoder().encodeToString(generateToken(user).getBytes());
    }

    private String generateToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validTime);
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        claims.put("roles", user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }
}
