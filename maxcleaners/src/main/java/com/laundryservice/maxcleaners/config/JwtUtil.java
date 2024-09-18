package com.laundryservice.maxcleaners.config;

import com.laundryservice.maxcleaners.model.Customer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Author Tejesh
 */
@Component
public class JwtUtil {

    @Value("${spring.security.secret-key}")
    private String secretKey; // Ensure this is kept secure
    private final Set<String> blacklistedTokens = new HashSet<>();
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public String generateToken(String phoneNumber) {
        return Jwts.builder()
                .setSubject(phoneNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
                .signWith(SignatureAlgorithm.HS512, secretKey) // Using HS512 for consistency
                .compact();
    }

    public String generateToken(Customer customer) {
        logger.info("testing purpose secret key::; ", secretKey);
        logger.info("Generating token for customer with phone number: {}", customer.getPhoneNumber());
        return Jwts.builder()
                .setSubject(customer.getPhoneNumber())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 min validity
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }


    public String extractPhoneNumber(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public boolean isTokenValid(String token, String phoneNumber) {
        boolean isValid =extractPhoneNumber(token).equals(phoneNumber) && !isTokenExpired(token) && !isTokenBlacklisted(token);
        logger.info("Token validation result for customer {}: {}", phoneNumber, isValid);
        return isValid;
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }


    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
        logger.info("Token added to blacklist: {}", token);
    }
}
