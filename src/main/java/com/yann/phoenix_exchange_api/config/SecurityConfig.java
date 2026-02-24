package com.yann.phoenix_exchange_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Actuator (if enabled)
                        .requestMatchers("/actuator/**").permitAll()

                        // Products endpoints
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL", "TECHNICIAN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasRole("ADMIN")

                        // Repair Tickets endpoints
                        .requestMatchers("/api/repair-tickets/**")
                        .hasAnyRole("ADMIN", "MANAGER", "TECHNICIAN")

                        // Smart Valuator endpoints
                        .requestMatchers("/api/valuator/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL", "TECHNICIAN")

                        // Sales endpoints
                        .requestMatchers("/api/sales-orders/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")

                        // Purchase endpoints
                        .requestMatchers("/api/purchase-orders/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")

                        // Suppliers endpoints
                        .requestMatchers("/api/suppliers/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")

                        // Customers endpoints
                        .requestMatchers("/api/customers/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")

                        // Warehouses endpoints
                        .requestMatchers("/api/warehouses/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // Dashboard endpoints
                        .requestMatchers("/api/dashboard/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // Users management
                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}