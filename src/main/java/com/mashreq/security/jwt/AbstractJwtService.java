package com.mashreq.security.jwt;

import com.mashreq.common.I18n;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Common JWT Service for handling JWT token creation and parsing.
 *
 */
@Slf4j
public abstract class AbstractJwtService implements TokenService {

  protected Payload createPayload(
      String aud, String subject, Long expirationMillis, Map<String, Object> claimMap) {

    var builder = new JWTClaimsSet.Builder();

    builder
        // .issueTime(new Date())
        .expirationTime(new Date(System.currentTimeMillis() + expirationMillis))
        .audience(aud)
        .subject(subject)
        .claim(MASHREQ_IAT, System.currentTimeMillis());

    claimMap.forEach(builder::claim);

    var claims = builder.build();

    return new Payload(claims.toJSONObject());
  }

  @Override
  public String createToken(String audience, String subject, Long expirationMillis) {

    return createToken(audience, subject, expirationMillis, new HashMap<>());
  }

  @Override
  public JWTClaimsSet parseToken(String token, String audience) {

    var claims = parseToken(token);
    if (audience == null || !claims.getAudience().contains(audience)) {
      throw new BadCredentialsException(I18n.getMessage("error.token.wrongAudience"));
    }

    var expirationTime = claims.getExpirationTime().getTime();
    var currentTime = System.currentTimeMillis();

    log.debug("Parsing JWT. Expiration time = {}. Current time = {}", expirationTime, currentTime);

    if (expirationTime <= currentTime) {
      log.error("Token expired time = {}. Current time = {}", expirationTime, currentTime);
      throw new BadCredentialsException(I18n.getMessage("error.token.expired"));
    }

    return claims;
  }

  @Override
  public JWTClaimsSet parseToken(String token, String audience, long issuedAfter) {

    var claims = parseToken(token, audience);

    var issueTime = (long) claims.getClaim(MASHREQ_IAT);
    if (issueTime <= issuedAfter) {
      throw new BadCredentialsException(I18n.getMessage("error.token.obsolete"));
    }

    return claims;
  }

  protected abstract JWTClaimsSet parseToken(String token);

  @Override
  public <T> T parseClaim(String token, String claim) {

    var claims = parseToken(token);
    return (T) claims.getClaim(claim);
  }
}
