package com.mashreq.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Common Properties for the application.
 *
 */
@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@Slf4j
public class ApplicationProperties {
  private Web web = new Web();
  /** CORS related properties */
  private Cors cors = new Cors();

  private Jwt jwt;

  public ApplicationProperties() {
    log.info("ApplicationProperties Created");
  }

  /** Web application related properties. */
  @Getter
  @Setter
  public static class Web {
    /**
     * Client web application's base URL. Used in the verification link mailed to the users, etc.
     */
    private String url;
  }

  /** CORS configuration related properties */
  @Getter
  @Setter
  public static class Cors {

    /**
     * Comma separated whitelisted URLs for CORS.
     *
     * <p>Should contain the applicationURL at the minimum.
     *
     * <p>Not providing this property would disable CORS configuration.
     */
    private String[] allowedOrigins;

    /** Methods to be allowed, e.g. GET,POST,... */
    private String[] allowedMethods = {
      "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "PATCH"
    };

    /** Request headers to be allowed, e.g. content-type,accept,origin,x-requested-with,... */
    private String[] allowedHeaders = {
      "Accept",
      "Accept-Encoding",
      "Accept-Language",
      "Cache-Control",
      "Connection",
      "Content-Length",
      "Content-Type",
      "Cookie",
      "Host",
      "Origin",
      "Pragma",
      "Referer",
      "User-Agent",
      "x-requested-with",
      "e2e-test",
      HttpHeaders.AUTHORIZATION
    };

    /**
     * Response headers that you want to expose to the FE client. Still, by default, we have
     * provided most of the common headers. <br>
     * See <a
     * href="http://stackoverflow.com/questions/25673089/why-is-access-control-expose-headers-needed#answer-25673446">
     * here</a> to know why this could be needed.
     */
    private String[] exposedHeaders = {
      "Cache-Control",
      "Connection",
      "Content-Type",
      "Date",
      "Expires",
      "Pragma",
      "Server",
      "Set-Cookie",
      "Transfer-Encoding",
      "X-Content-Type-Options",
      "X-XSS-Protection",
      "X-Frame-Options",
      "X-Application-Context",
      HttpHeaders.AUTHORIZATION
    };

    /** CORS <code>maxAge</code> long property */
    private long maxAge = 3600L;
  }

  /** Properties related to JWT token generation */
  @Getter
  @Setter
  public static class Jwt {

    /** Secret for signing JWT */
    private String secret;

    private Expiry expiry = new Expiry();

    @Getter
    @Setter
    public static class Expiry {
      // Expiration milliseconds for authentication token  (30 min)
      private long authToken = 1800000L;
    }
  }
}
