package com.aluraCursos.ForoHub.infra.security;

import com.aluraCursos.ForoHub.repository.UsuarioRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        
        // Skip token validation for public endpoints
        if (isPublicEndpoint(requestPath, request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
                return;
            }
            
            String token = authHeader.replace("Bearer ", "").trim();
            if (token.isEmpty()) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Empty token");
                return;
            }
            
            String nombreUsuario = tokenService.getSubject(token);
            if (nombreUsuario == null) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
                return;
            }
            
            var usuario = usuarioRepository.findByLogin(nombreUsuario);
            if (usuario == null) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "User not found");
                return;
            }
            
            var authentication = new UsernamePasswordAuthenticationToken(
                usuario, 
                null,
                usuario.getAuthorities()
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            
        } catch (JWTVerificationException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Authentication error", e);
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during authentication");
        }
    }

    private boolean isPublicEndpoint(String path, String method) {
        return "/login".equals(path) && "POST".equalsIgnoreCase(method) ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars");
    }



    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
