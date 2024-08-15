package com.mashreq.security;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class OpenApiSecurityConfig {

  private static final String[] OPENAPI_ENDPOINTS =
      new String[]{"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"};
  private final PasswordEncoder passwordEncoder;
  @Value("${openapi.user}")
  private String openapiUser;

  @Value("${openapi.password}")
  private String openapiPassword;

  /**
   * Autowired constructor.
   *
   * @param passwordEncoder a PasswordEncoder
   */
  public OpenApiSecurityConfig(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @SneakyThrows
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .logout(LogoutConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(OPENAPI_ENDPOINTS).hasRole("OPENAPI")
            .anyRequest().authenticated())
        .addFilterBefore(new BasicAuthenticationFilter(openApiAuthenticationManager(http, passwordEncoder)), BasicAuthenticationFilter.class); // Configures basic authentication

    return http.build();
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
