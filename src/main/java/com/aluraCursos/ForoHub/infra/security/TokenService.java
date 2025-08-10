package com.aluraCursos.ForoHub.infra.security;

import com.aluraCursos.ForoHub.domain.usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;



@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Value("${api.security.secret}")
    private String apiSecret;

    private static final String ISSUER = "ForoHub";

    public String generarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getLogin())
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(generarFechaExpiracion())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            logger.error("Error generating token", exception);
            throw new RuntimeException("Error generating token", exception);
        }
    }

    public String getSubject(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JWTVerificationException("Token cannot be null or empty");
        }
        
        // Basic JWT format validation
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new JWTVerificationException("Invalid token format. Expected 3 parts separated by dots.");
        }
        
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            DecodedJWT verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);
                    
            String subject = verifier.getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                throw new JWTVerificationException("Token subject is empty");
            }
            
            return subject;
        } catch (JWTVerificationException exception) {
            // Log the specific verification error
            System.err.println("JWT Verification failed: " + exception.getMessage());
            throw exception; // Re-throw to be handled by the caller
        } catch (Exception e) {
            System.err.println("Unexpected error during token validation: " + e.getMessage());
            throw new JWTVerificationException("Token validation failed", e);
        }
    }

    private Instant generarFechaExpiracion() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-04:00"));
    }

}