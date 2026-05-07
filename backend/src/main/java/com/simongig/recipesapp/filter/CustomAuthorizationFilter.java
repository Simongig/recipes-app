package com.simongig.recipesapp.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION; 

public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final Algorithm algorithm;

    public CustomAuthorizationFilter(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if(request.getServletPath().equals("/api/auth/login") || request.getServletPath().equals("/api/v1/auth/token/refresh/**")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    System.out.println("--------- Start Authorization ---------");
                    String token = authorizationHeader.substring("Bearer ".length());
                    JWTVerifier verifier = JWT.require(this.algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    System.out.println(roles[0]);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    System.out.println("Authorities size: " + authorities.size());
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    System.out.println(authenticationToken.getName());
                    System.out.println(authenticationToken.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                    System.out.println("--------- End Authorization ---------");
                } catch (Exception exception) {
                    System.out.println("Error loggin in: " + exception.getMessage());
                    filterChain.doFilter(request, response);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
        
    }

    
}
