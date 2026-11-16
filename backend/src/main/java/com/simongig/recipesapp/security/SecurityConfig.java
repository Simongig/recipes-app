package com.simongig.recipesapp.security;

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
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.simongig.recipesapp.filter.CustomAuthenticationFilter;
import static com.simongig.recipesapp.model.UserRole.RoleName.ROLE_ADMIN;
import static com.simongig.recipesapp.model.UserRole.RoleName.ROLE_USER;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String REFRESH_PATH = "/api/v1/auth/token/refresh";

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
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager,
            JwtTokenService tokenService)
            throws Exception {
        CustomAuthenticationFilter authFilter = new CustomAuthenticationFilter(authenticationManager, tokenService);
        authFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", REFRESH_PATH).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/recipe/all", "/api/v1/recipe/id/*").permitAll()
                // Server-side Open Graph preview for social-media crawlers, see RecipeSocialPreviewController.
                .requestMatchers(HttpMethod.GET, "/recipe/id/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/recipe/search").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/recipe/add").hasAnyAuthority(ROLE_USER.name(), ROLE_ADMIN.name())
                // ml-service calls this back with generation results; it has no user JWT,
                // so it's authenticated via a shared secret header instead (see MealPlannerController).
                .requestMatchers(HttpMethod.PATCH, "/api/v1/mealplans/jobs/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/mealplans/*").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/v1/user**").hasAnyAuthority(ROLE_USER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/user/save/**").hasAnyAuthority(ROLE_ADMIN.name())
                .anyRequest().authenticated()
            )
            .addFilter(authFilter)
            .oauth2ResourceServer(oauth2 -> oauth2
                .bearerTokenResolver(bearerTokenResolver())
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Keeps your exact string mapping (e.g. "ROLE_USER") without appending unwanted prefixes
        grantedAuthoritiesConverter.setAuthorityPrefix(""); 
        grantedAuthoritiesConverter.setAuthoritiesClaimName(JwtTokenService.ROLES_CLAIM);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    // The refresh endpoint receives a refresh token as Bearer, which the resource server would
    // reject with 401 before the controller runs. Hide the header from it on that one route;
    // UserController verifies the token itself.
    private BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
        return request -> (request.getContextPath() + REFRESH_PATH).equals(request.getRequestURI())
                ? null
                : delegate.resolve(request);
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtTokenService tokenService) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(tokenService.secretKey()).build();
        // Refresh tokens carry the same signature; without this check they would pass as
        // (role-less) Bearer credentials for 30 days.
        OAuth2TokenValidator<Jwt> accessTokensOnly = new JwtClaimValidator<String>(
                JwtTokenService.TOKEN_TYPE_CLAIM, JwtTokenService.ACCESS::equals);
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(), accessTokensOnly));
        return decoder;
    }
}
