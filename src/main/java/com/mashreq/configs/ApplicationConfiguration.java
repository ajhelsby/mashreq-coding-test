package com.mashreq.configs;

import com.mashreq.common.I18n;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main application configuration.
 */
@Configuration
@EnableJpaAuditing
@Slf4j
public class ApplicationConfiguration implements WebMvcConfigurer {

  /**
   * Configures Password encoder.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {

    log.info("Configuring PasswordEncoder");
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /**
   * Configures I18n
   */
  @Bean
  public I18n i18n(MessageSource messageSource, LocalValidatorFactoryBean validator) {

    log.info("Configuring I18n");
    return new I18n(messageSource, validator);
  }

}
