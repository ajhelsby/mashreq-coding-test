package com.mashreq.security.jwt;

import com.mashreq.common.I18n;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * JWS (JSON Web Signature) Service.
 *
 * <p>Should be used for JWT and should not be used for encrypting any sensitive information.
 *
 * <p>Reference: https://connect2id.com/products/nimbus-jose-jwt/examples/jws-with-hmac
 */
@Slf4j
@Service
public class JwsService extends AbstractJwtService implements TokenService {

  public static String USER_CLAIM = "user";
  public static String AUTH_AUDIENCE = "auth";
  public static String REFRESH_AUTH_AUDIENCE = "auth-refresh";

  private final JWSSigner signer;
  private final JWSVerifier verifier;

  /**
   * Constructor.
   *
   * @param secret a default secret key for encryption
   * @throws JOSEException when wrong key supplied
   */
  public JwsService(@Value("${app.jwt.secret}") String secret) throws JOSEException {

    signer = new MACSigner(secret);
    verifier = new MACVerifier(secret);
  }

  @Override
  public String createToken(
      String aud, String subject, Long expirationMillis, Map<String, Object> claimMap) {

    Payload payload = createPayload(aud, subject, expirationMillis, claimMap);

    // Prepare JWS object
    JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), payload);

    try {
      // Apply the HMAC
      jwsObject.sign(signer);

    } catch (JOSEException e) {

      throw new RuntimeException(e);
    }

    // To serialize to compact form, produces something like
    // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
    return jwsObject.serialize();
  }

  /**
   * Parses a token.
   *
   * @param token a string representation of the token
   * @return a JWTClaimsSet
   */
  protected JWTClaimsSet parseToken(String token) {

    // Parse the JWS and verify it, e.g. on client-side
    JWSObject jwsObject;

    try {
      jwsObject = JWSObject.parse(token);
      if (jwsObject.verify(verifier)) {
        return JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
      }

    } catch (JOSEException | ParseException e) {
      log.error("Failed to parse token", e.getMessage());
      throw new BadCredentialsException(I18n.getMessage("error.token.parsed"));
    }

    throw new BadCredentialsException(I18n.getMessage("error.token.verification"));
  }
}
