package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.MyUserDetails;
import com.bida.employer.manager.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JWTUtilService {

    @Value(value = "${jwt.access.secret.key}")
    private String ACCESS_SECRET_KEY;

    @Value(value = "${jwt.access.available.time}")
    private long ACCESS_AVAILABLE_TIME;

    @Value(value = "${jwt.refresh.secret.key}")
    private String REFRESH_SECRET_KEY;

    @Value(value = "${jwt.refresh.available.time}")
    private long REFRESH_AVAILABLE_TIME;

    public UUID extractId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getId));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(ACCESS_SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public UUID extractIdRefreshToken(String token) {
        return UUID.fromString(extractClaimRefreshToken(token, Claims::getId));
    }

    public <T> T extractClaimRefreshToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsRefreshToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaimsRefreshToken(String token) {
        return Jwts.parser().setSigningKey(REFRESH_SECRET_KEY).parseClaimsJws(token).getBody();
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final UUID id = extractId(token);
        return id.equals(((MyUserDetails) userDetails).getUser().getId()) && !isTokenExpired(token);
    }

    public Boolean validateRefreshToken(String token, UserDetails userDetails) {
        final UUID id = extractIdRefreshToken(token);
        return id.equals(((MyUserDetails)userDetails).getUser().getId()) && !isTokenExpired(token);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        User user = ((MyUserDetails)userDetails).getUser();
        claims.put("userId", user.getId());
        claims.put("organizationId", user.getOrganizationId());
        claims.put("role", user.getUserRole());
        claims.put("email", user.getEmail());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());

        return Jwts.builder().setClaims(claims).setId(user.getId().toString()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_AVAILABLE_TIME))
                .signWith(SignatureAlgorithm.HS256, ACCESS_SECRET_KEY).compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        User user = ((MyUserDetails)userDetails).getUser();

        return Jwts.builder().setClaims(claims).setId(user.getId().toString()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_AVAILABLE_TIME))
                .signWith(SignatureAlgorithm.HS256, REFRESH_SECRET_KEY).compact();
    }
}
