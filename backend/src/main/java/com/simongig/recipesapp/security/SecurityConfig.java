package com.simongig.recipesapp.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.auth0.jwt.algorithms.Algorithm;
import com.simongig.recipesapp.filter.CustomAuthenticationFilter;
import static com.simongig.recipesapp.model.UserRole.RoleName.ROLE_ADMIN;
import static com.simongig.recipesapp.model.UserRole.RoleName.ROLE_USER;

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
        authFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/token/refresh").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/recipe/all", "/api/v1/recipe/id/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/recipe/search").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/recipe/add").hasAnyAuthority(ROLE_USER.name(), ROLE_ADMIN.name())
                .requestMatchers(HttpMethod.GET,  "/api/v1/user**").hasAnyAuthority(ROLE_USER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/user/save/**").hasAnyAuthority(ROLE_ADMIN.name())
                .anyRequest().authenticated()
            )
            .addFilter(authFilter)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public Algorithm jwtAlgorithm() {
        return Algorithm.HMAC256(jwtSecret.getBytes());
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Keeps your exact string mapping (e.g. "ROLE_USER") without appending unwanted prefixes
        grantedAuthoritiesConverter.setAuthorityPrefix(""); 
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
