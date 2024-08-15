package com.mashreq.configs;

import com.mashreq.authentication.jpa.JpaSecurityConfig;
import com.mashreq.security.OpenApiSecurityConfig;
import com.mashreq.security.SimpleAccessDeniedHandler;
import com.mashreq.security.SimpleAuthenticationEntryPoint;
import com.mashreq.security.jwt.JwsService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.security.core.userdetails.UserDetailsService;
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

  /**
   * Default security configuration for the APIs.
   *
   * <p>Loaded conditionally when the property <code>modules.auth</code> isn't defined or set to
   * JPA.
   */
  @Bean
  @ConditionalOnProperty(value = "modules.auth", havingValue = "JPA", matchIfMissing = true)
  public JpaSecurityConfig jpaSecurityConfig(
      SimpleAuthenticationEntryPoint authenticationErrorHandler,
      SimpleAccessDeniedHandler accessDeniedHandler,
      @Qualifier("jpaUserDetailsService") UserDetailsService userDetailsService,
      JwsService jwsService,
      PasswordEncoder passwordEncoder) {

    log.info("Configuring JpaSecurityConfig");
    return new JpaSecurityConfig(
        authenticationErrorHandler,
        accessDeniedHandler,
        userDetailsService,
        jwsService,
        passwordEncoder);
  }

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
  public OpenApiSecurityConfig openApiSecurityConfig(PasswordEncoder passwordEncoder) {
    return new OpenApiSecurityConfig(passwordEncoder);
  }
}

