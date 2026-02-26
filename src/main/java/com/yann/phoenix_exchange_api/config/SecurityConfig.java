package com.yann.phoenix_exchange_api.config;

import com.yann.phoenix_exchange_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Actuator
                        .requestMatchers("/actuator/**").permitAll()

                        // Products
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL", "TECHNICIAN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasRole("ADMIN")

                        // Repair Tickets
                        .requestMatchers("/api/repair-tickets/**")
                        .hasAnyRole("ADMIN", "MANAGER", "TECHNICIAN")

                        // Smart Valuator
                        .requestMatchers("/api/valuator/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL", "TECHNICIAN")

                        // Sales
                        .requestMatchers("/api/sales-orders/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")

                        // Purchases
                        .requestMatchers("/api/purchase-orders/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")

                        // Suppliers
                        .requestMatchers("/api/suppliers/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")

                        // Customers
                        .requestMatchers("/api/customers/**")
                        .hasAnyRole("ADMIN", "MANAGER", "COMMERCIAL")

                        // Warehouses
                        .requestMatchers("/api/warehouses/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // Dashboard
                        .requestMatchers("/api/dashboard/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // Users
                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                        // All other requests
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}