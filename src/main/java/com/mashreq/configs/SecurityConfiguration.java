package com.mashreq.configs;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Main security configuration for the application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@AutoConfigureBefore({
    WebMvcAutoConfiguration.class,
    ErrorMvcAutoConfiguration.class,
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class,
    ApplicationConfiguration.class
})
@Slf4j
public class SecurityConfiguration {

  @Value("${openapi.user}")
  private String openapiUser;

  @Value("${openapi.password}")
  private String openapiPassword;

  /**
   * Configure CORS configuration for the application.
   */
  @Bean
  @ConditionalOnMissingBean(CorsConfigurationSource.class)
  protected CorsConfigurationSource corsConfigurationSource(ApplicationProperties appProperties) {
    log.info("Configuring CorsConfigurationSource");
    CorsConfiguration configuration = new CorsConfiguration();
    var cors = appProperties.getCors();
    configuration.setAllowedOriginPatterns(List.of(cors.getAllowedOrigins()));
    configuration.setAllowedMethods(List.of(cors.getAllowedMethods()));
    configuration.setAllowedHeaders(List.of(cors.getAllowedHeaders()));
    configuration.setExposedHeaders(Arrays.asList(cors.getExposedHeaders()));
    configuration.setMaxAge(cors.getMaxAge());

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
  }

  /**
   * Security configuration for Swagger.
   */
  @Bean
  @Order(1)
  public SecurityFilterChain openApiSecurity(HttpSecurity http, PasswordEncoder passwordEncoder)
      throws Exception {
    // Define the endpoints that should be matched for OpenAPI
    String[] openApiEndpoints = new String[]{"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**"};

    // Configure the HttpSecurity for OpenAPI endpoints
    http
        .securityMatcher(request -> {
          for (String endpoint : openApiEndpoints) {
            if (request.getServletPath().startsWith(endpoint)) {
              return true;
            }
          }
          return false;
        })
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(openApiEndpoints).hasRole("OPENAPI")
            .anyRequest().authenticated()
        )
        .csrf(AbstractHttpConfigurer::disable)
        .addFilterBefore(new BasicAuthenticationFilter(openApiAuthenticationManager(http, passwordEncoder)), BasicAuthenticationFilter.class); // Configures basic authentication


    return http.getOrBuild();
  }

  /**
   * Configures the authentication manager with an in-memory user for OpenAPI.
   */
  @Bean
  public AuthenticationManager openApiAuthenticationManager(
      HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder
        .inMemoryAuthentication()
        .withUser(openapiUser)
        .password(passwordEncoder.encode(openapiPassword))
        .roles("OPENAPI");
    return authenticationManagerBuilder.build();
  }
}

