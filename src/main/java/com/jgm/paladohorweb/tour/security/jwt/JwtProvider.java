package com.jgm.paladohorweb.tour.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        System.out.println("JWT_SECRET length = " + (jwtSecret == null ? "null" : jwtSecret.length()));
        System.out.println("JWT_SECRET preview=" + jwtSecret.substring(0, 5) + "..." + jwtSecret.substring(jwtSecret.length()-5));
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret); // ✅ correcto si JWT_SECRET es base64
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String rol) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRolFromToken(String token) {
        return getClaims(token).get("rol", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT no soportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("JWT malformado: " + e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("Firma JWT inválida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT vacío/ilegal: " + e.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
