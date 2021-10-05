package com.kadry.blog.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    public static final String AUTHORITIES_KEY = "authorities";
    final int expirationDuration = 7 * 24 * 60 * 60 * 1000;

    @Value("${secret_key}")
    private final String secretKey;

    private final JwtParser jwtParser;

    public TokenProvider(@Value("${secret_key}") String secretKey) {
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey).build();
        this.secretKey = secretKey;
    }

    public String generateToken(Authentication authentication){
        String authorities = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        return Jwts.builder()
                    .setSubject(authentication.getName())
                    .setExpiration(new Date(now + expirationDuration))
                    .claim(AUTHORITIES_KEY, authorities)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
    }

    public boolean validate(String token){
        try{
            jwtParser.parseClaimsJws(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            e.printStackTrace();
        }
        return false;
    }

    public Authentication getAuthentication(String token){
        Claims claims =jwtParser.parseClaimsJws(token).getBody();
        Collection<GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                    .filter(auth -> auth.trim().isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(claims.getSubject(),token, authorities);
    }
}
