# 🔥 PHOENIX EXCHANGE - EXCEPTIONS, CONFIG & SECURITY COMPLETS

> **Package** : `com.phoenix.exchange`
> **Contenu** : Exception Handlers + Configuration + Security JWT

---

## 📋 TABLE DES MATIÈRES

### PARTIE 1 : EXCEPTIONS
1. [Exceptions Personnalisées](#exceptions-personnalisées)
2. [GlobalExceptionHandler](#globalexceptionhandler)

### PARTIE 2 : CONFIGURATION
3. [SecurityConfig](#securityconfig)
4. [JpaConfig](#jpaconfig)
5. [CorsConfig](#corsconfig)
6. [SwaggerConfig](#swaggerconfig)
7. [WebConfig](#webconfig)

### PARTIE 3 : SECURITY JWT
8. [JwtTokenProvider](#jwttokenprovider)
9. [JwtAuthenticationFilter](#jwtauthenticationfilter)
10. [CustomUserDetailsService](#customuserdetailsservice)
11. [SecurityUtils](#securityutils)
12. [AuthService](#authservice)

---

## PARTIE 1 : EXCEPTIONS

### Exceptions Personnalisées

#### ResourceNotFoundException

```java
package com.phoenix.exchange.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: '%s'", resource, field, value));
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### BusinessException

```java
package com.phoenix.exchange.exception;

public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
```

#### UnauthorizedException

```java
package com.phoenix.exchange.exception;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### ValidationException

```java
package com.phoenix.exchange.exception;

import java.util.Map;

public class ValidationException extends RuntimeException {
    
    private final Map<String, String> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = Map.of();
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
}
```

#### InsufficientStockException

```java
package com.phoenix.exchange.exception;

public class InsufficientStockException extends BusinessException {
    
    public InsufficientStockException(String message) {
        super(message, "INSUFFICIENT_STOCK");
    }
    
    public InsufficientStockException(Long productId, Integer requested, Integer available) {
        super(String.format("Insufficient stock for product %d. Requested: %d, Available: %d", 
            productId, requested, available), "INSUFFICIENT_STOCK");
    }
}
```

#### InvalidStateTransitionException

```java
package com.phoenix.exchange.exception;

public class InvalidStateTransitionException extends BusinessException {
    
    public InvalidStateTransitionException(String message) {
        super(message, "INVALID_STATE_TRANSITION");
    }
    
    public InvalidStateTransitionException(String entityType, Object currentState, Object targetState) {
        super(String.format("Cannot transition %s from %s to %s", 
            entityType, currentState, targetState), "INVALID_STATE_TRANSITION");
    }
}
```

---

### GlobalExceptionHandler

```java
package com.phoenix.exchange.exception;

import com.phoenix.exchange.dto.common.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            WebRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle BusinessException (400)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(
            BusinessException ex, 
            WebRequest request) {
        
        log.error("Business error: {} (code: {})", ex.getMessage(), ex.getErrorCode());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Business Error")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .details(List.of(ex.getErrorCode()))
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle ValidationException (400)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex, 
            WebRequest request) {
        
        log.error("Validation error: {}", ex.getMessage());
        
        List<String> details = ex.getErrors().entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.toList());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .details(details)
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle @Valid validation errors (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        log.error("Validation errors: {}", errors);
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid input data")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .details(errors)
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle UnauthorizedException (401)
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(
            UnauthorizedException ex, 
            WebRequest request) {
        
        log.error("Unauthorized access: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Unauthorized")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handle BadCredentialsException (401)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentialsException(
            BadCredentialsException ex, 
            WebRequest request) {
        
        log.error("Bad credentials: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Bad Credentials")
            .message("Invalid username or password")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handle AuthenticationException (401)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex, 
            WebRequest request) {
        
        log.error("Authentication failed: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Authentication Failed")
            .message("Authentication failed: " + ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handle AccessDeniedException (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(
            AccessDeniedException ex, 
            WebRequest request) {
        
        log.error("Access denied: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("Access Denied")
            .message("You don't have permission to access this resource")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handle IllegalArgumentException (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            WebRequest request) {
        
        log.error("Illegal argument: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Argument")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle IllegalStateException (409)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalStateException(
            IllegalStateException ex, 
            WebRequest request) {
        
        log.error("Illegal state: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.CONFLICT.value())
            .error("Conflict")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    /**
     * Handle all other exceptions (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex, 
            WebRequest request) {
        
        log.error("Internal server error: ", ex);
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred. Please try again later.")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

---

## PARTIE 2 : CONFIGURATION

### SecurityConfig

```java
package com.phoenix.exchange.config;

import com.phoenix.exchange.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
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
```

---

### JpaConfig

```java
package com.phoenix.exchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.phoenix.exchange.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    // JPA configuration
}
```

---

### CorsConfig

```java
package com.phoenix.exchange.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins (Angular frontend)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "http://localhost:4201",
            "https://phoenix-exchange.com" // Production URL
        ));
        
        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Expose headers
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Max age for preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
```

---

### SwaggerConfig

```java
package com.phoenix.exchange.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .info(new Info()
                .title("Phoenix Exchange API")
                .description("API de gestion du cycle de vie des équipements électroniques reconditionnés")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Phoenix Exchange")
                    .email("contact@phoenix-exchange.com")
                    .url("https://phoenix-exchange.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Entrez le token JWT sans le préfixe 'Bearer '")));
    }
}
```

---

### WebConfig

```java
package com.phoenix.exchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Additional web configuration if needed
}
```

---

## PARTIE 3 : SECURITY JWT

### JwtTokenProvider

```java
package com.phoenix.exchange.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration; // in milliseconds (e.g., 28800000 = 8 hours)
    
    /**
     * Generate JWT token from Authentication
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername());
    }
    
    /**
     * Generate JWT token from username
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.getSubject();
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("JWT validation error: {}", ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.getExpiration();
    }
}
```

---

### JwtAuthenticationFilter

```java
package com.phoenix.exchange.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Set authentication for user: {}", username);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
```

---

### CustomUserDetailsService

```java
package com.phoenix.exchange.security;

import com.phoenix.exchange.entity.User;
import com.phoenix.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return buildUserDetails(user);
    }
    
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        return buildUserDetails(user);
    }
    
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .authorities(getAuthorities(user))
            .accountExpired(false)
            .accountLocked(!user.getIsActive())
            .credentialsExpired(false)
            .disabled(!user.getIsActive())
            .build();
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
```

---

### SecurityUtils

```java
package com.phoenix.exchange.security;

import com.phoenix.exchange.entity.User;
import com.phoenix.exchange.entity.enums.UserRole;
import com.phoenix.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    
    private final UserRepository userRepository;
    
    /**
     * Get currently authenticated user
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }
    
    /**
     * Get currently authenticated username
     */
    public Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        
        return Optional.of(authentication.getName());
    }
    
    /**
     * Get currently authenticated user ID
     */
    public Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(User::getId);
    }
    
    /**
     * Check if current user has specific role
     */
    public boolean hasRole(UserRole role) {
        return getCurrentUser()
            .map(user -> user.getRole() == role)
            .orElse(false);
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public boolean hasAnyRole(UserRole... roles) {
        Optional<User> currentUser = getCurrentUser();
        if (currentUser.isEmpty()) {
            return false;
        }
        
        UserRole userRole = currentUser.get().getRole();
        for (UserRole role : roles) {
            if (userRole == role) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !authentication.getPrincipal().equals("anonymousUser");
    }
}
```

---

### AuthService

```java
package com.phoenix.exchange.service;

import com.phoenix.exchange.dto.auth.*;
import com.phoenix.exchange.entity.User;
import com.phoenix.exchange.entity.enums.UserRole;
import com.phoenix.exchange.repository.UserRepository;
import com.phoenix.exchange.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Login user and return JWT token
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        