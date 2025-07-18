package com.code.camping.security;

import com.code.camping.entity.Admin;
import com.code.camping.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {
    
    @Value("${jwt.secret:java-incubation-25-final-project-team-2}")
    private String jwtSignatureSecret;
    
    @Value("${jwt.expiration:86400000}")
    private int jwtExpirationInMs;

    public SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSignatureSecret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey())
                .build().parseSignedClaims(token).getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        return createToken(claims, user.getEmail());
    }

    public String generateAccessTokenForAdmin(Admin admin){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        return createToken(claims, admin.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpirationInMs);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Claims decodeAccessToken(String accessToken){
        return Jwts.parser().verifyWith(getSigningKey())
                .build().parseSignedClaims(accessToken).getPayload();
    }
}
