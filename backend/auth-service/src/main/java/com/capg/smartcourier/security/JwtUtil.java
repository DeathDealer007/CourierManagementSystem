package com.capg.smartcourier.security;

import java.security.Key; // this is a normal java security interface that is used for representing the cryptographic key
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//this is where we get the classes for creating, parsing and validating our tokens
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; //this is Jwt's helper class that helps in creating the secure keys.


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;
    /*
     * key - used for representing the cryptographic key
     * Keys - this keys is a helper class from jsonwebtoken and it is used for creating the secure key.
     */

    private Key getSignKey() { // here string secret is going to be converted into cryptographic key 
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String username, Long userId, java.util.List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }
}