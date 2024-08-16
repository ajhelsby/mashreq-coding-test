package com.mashreq.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Default security configuration for the application.
 *
 * <p>Override protected method if you need to customise anything.
 *
 */
public class BaseSecurityConfig {

  public static final String[] PUBLIC = new String[]{
      "/health",
      "/actuator/health",
      "/api/v1/auth/**",
      "/api/v1/users/init",
      "/swagger-resources/**",
      "/swagger-ui.html",
      "/swagger-ui/index.html",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "/login"
  };

  private final SimpleAuthenticationEntryPoint authenticationErrorHandler;
  private final SimpleAccessDeniedHandler accessDeniedHandler;

  public BaseSecurityConfig(
      SimpleAuthenticationEntryPoint authenticationErrorHandler,
      SimpleAccessDeniedHandler accessDeniedHandler) {
    this.authenticationErrorHandler = authenticationErrorHandler;
    this.accessDeniedHandler = accessDeniedHandler;
  }

  @SneakyThrows
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(this::configureExceptionHandling)
        .logout(LogoutConfigurer::disable)
        .authorizeHttpRequests(this::configureAuthorization)
        .addFilterBefore(new CustomTokenAuthenticationFilter(), BasicAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Configures exception-handling.
   */
  public void configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> exceptions) {
    exceptions
        .authenticationEntryPoint(authenticationErrorHandler)
        .accessDeniedHandler(accessDeniedHandler);
  }

  /**
   * Configure URLs for authorization.
   */
  public void configureAuthorization(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authz) {
    authz
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        .requestMatchers(PUBLIC).permitAll()
        .anyRequest().authenticated();
  }

  public class CustomTokenAuthenticationFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
      chain.doFilter(request, response);
    }
  }
}
