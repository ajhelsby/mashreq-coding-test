package com.mashreq.security.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Map;

/**
 * TokenService interface for JWT token service.
 *
 */
public interface TokenService {

  // issued at claim to be used in JWT token
  String MASHREQ_IAT = "mashreq-iat";

  /**
   * Creates new token from given parameters.
   *
   * @param aud a token audience
   * @param subject a token subject
   * @param expirationMillis a token expiry duration
   * @param claimMap a map of claims
   * @return a string representation of the token
   */
  String createToken(
      String aud, String subject, Long expirationMillis, Map<String, Object> claimMap);

  /**
   * Creates new token from given parameters.
   *
   * @param audience a token audience
   * @param subject a token subject
   * @param expirationMillis a token expiry duration
   * @return a string representation of the token
   */
  String createToken(String audience, String subject, Long expirationMillis);

  /**
   * Parses given token.
   *
   * @param token a string representation of the token
   * @param audience an audience
   * @return a JWTClaimsSet
   */
  JWTClaimsSet parseToken(String token, String audience);

  /**
   * Parses given token.
   *
   * @param token a string representation of the token
   * @param audience an audience
   * @param issuedAfter a time when token was issued
   * @return a JWTClaimsSet
   */
  JWTClaimsSet parseToken(String token, String audience, long issuedAfter);

  /**
   * Parses given token and claims.
   *
   * @param token a string representation of the token
   * @param claim a claim to extract
   * @param <T> a generic class
   * @return a claim set of type T
   */
  <T> T parseClaim(String token, String claim);
}
