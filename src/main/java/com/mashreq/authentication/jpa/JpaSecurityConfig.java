package com.mashreq.authentication.jpa;

import com.mashreq.security.BaseSecurityConfig;
import com.mashreq.security.SecurityUtils;
import com.mashreq.security.SimpleAccessDeniedHandler;
import com.mashreq.security.SimpleAuthenticationEntryPoint;
import com.mashreq.security.jwt.JwsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

/**
 * JPA implementation of the security configuration.
 *
 * <p>This security implementation uses users from the database.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class JpaSecurityConfig extends BaseSecurityConfig {

  private final UserDetailsService userDetailsService;
  private final JwsService jwsService;
  private final PasswordEncoder passwordEncoder;

  public JpaSecurityConfig(
      SimpleAuthenticationEntryPoint authenticationErrorHandler,
      SimpleAccessDeniedHandler accessDeniedHandler,
      UserDetailsService userDetailsService,
      JwsService jwsService,
      PasswordEncoder passwordEncoder) {
    super(authenticationErrorHandler, accessDeniedHandler);
    this.userDetailsService = userDetailsService;
    this.jwsService = jwsService;
    this.passwordEncoder = passwordEncoder;
    log.info("JpaSecurityConfig created");
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(this::configureExceptionHandling)
        .authorizeHttpRequests(this::configureAuthorization)
        .addFilterBefore(new JwtAuthenticationFilter(userDetailsService, jwsService),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  protected void configureExceptionHandling(HttpSecurity http) throws Exception {
    http.exceptionHandling(exceptionHandling ->
        exceptionHandling
            .authenticationEntryPoint(authenticationErrorHandler())
            .accessDeniedHandler(accessDeniedHandler())
    );
  }

  protected void configureAuthorization(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authz -> authz
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        .requestMatchers(PUBLIC).permitAll()
        .anyRequest().authenticated());
  }

  public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (request, response, authentication) -> {
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write("Authentication successful");
    };
  }

  public AuthenticationFailureHandler authenticationFailureHandler() {
    return (request, response, exception) -> {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Authentication failed: " + exception.getMessage());
    };
  }

  public AuthenticationEntryPoint authenticationErrorHandler() {
    return (request, response, authException) -> {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + authException.getMessage());
    };
  }

  public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: " + accessDeniedException.getMessage());
    };
  }
}
