package com.chatservice.config.utils;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class UserJwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(UserJwtUtils.class);

    @Value("${jwt.secret_key}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    @Value("${jwt.jwtUserCookieName}")
    private String jwtCookie;

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token from which to extract the username.
     * @return The extracted username from the JWT token.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Generates a secret key for signing and verifying JWT tokens.
     *
     * @return A Key object representing the secret key used for JWT token operations.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Validates a JWT token by parsing and verifying its signature.
     *
     * @param authToken The JWT token to be validated.
     * @return true if the token is valid, false otherwise.
     * @throws MalformedJwtException if the token is malformed.
     * @throws ExpiredJwtException if the token is expired.
     * @throws UnsupportedJwtException if the token is unsupported.
     * @throws IllegalArgumentException if the claims string is empty.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException(e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Generates a JWT token using the provided username.
     *
     * @param username The username for which the token is generated.
     * @return The generated JWT token.
     */
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token using the provided UserDetails object.
     *
     * @param userDetails An object of type UserDetails that contains the user details for which the token is generated.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token using the provided extra claims and user details.
     *
     * @param extraClaims the extra claims to include in the token
     * @param userDetails the user details used to set the subject of the token
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365)) //1 year expiration
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * Generates a secret key for signing and verifying JWT tokens.
     *
     * @return The generated secret key used for signing and verifying JWT tokens.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a refresh token for the provided user.
     *
     * @param user The UserDetails object for which the refresh token is generated.
     * @return The generated refresh token.
     */
    public String generateRefreshToken(UserDetails user) {
        return generateToken(new HashMap<>(), user);
    }

}

