package com.rollcall.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static String secret = "replace_this_with_a_long_random_secret_in_application_yml";
    private static long expirationMs = 1000L * 60 * 60 * 24; // 24h

    public static void setSecret(String s) { secret = s; }
    public static void setExpirationMs(long ms) { expirationMs = ms; }

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }
}
