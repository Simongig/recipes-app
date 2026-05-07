package com.simongig.recipesapp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.algorithms.Algorithm;
import com.simongig.recipesapp.filter.CustomAuthenticationFilter;
import com.simongig.recipesapp.filter.CustomAuthorizationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) 
            throws Exception {
        CustomAuthenticationFilter authFilter = new CustomAuthenticationFilter(authenticationManager, jwtAlgorithm());
        authFilter.setFilterProcessesUrl("/api/auth/login");

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, // auth
                    "/api/auth/login",
                    "/api/v1/auth/token/refresh/**",
                    "/api/v1/auth/token/refresh",
                    "/api/v1/auth/user/**")
                    .permitAll()
                .requestMatchers(HttpMethod.GET, // auth
                    "/api/auth/login",
                    "/api/v1/auth/token/refresh/**",
                    "/api/v1/auth/user/**")
                    .permitAll()
                .requestMatchers( // content
                     "/api/v1/recipe/all")
                     .permitAll()
                .requestMatchers(HttpMethod.POST, // content
                    "/api/v1/recipe/add")
                    .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, 
                    "/api/v1/auth/user/**")
                    .hasAnyAuthority("ROLE_USER")
                .requestMatchers(HttpMethod.POST, 
                    "/api/v1/auth/user/save/**")
                    .hasAnyAuthority("ROLE_ADMIN")
                .anyRequest()
                .authenticated()
            )
            .addFilter(authFilter)
            .addFilterBefore(new CustomAuthorizationFilter(jwtAlgorithm()),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public Algorithm jwtAlgorithm() {
        return Algorithm.HMAC256(jwtSecret.getBytes());
    }
}
