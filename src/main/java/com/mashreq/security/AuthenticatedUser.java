package com.mashreq.security;

import com.mashreq.users.User;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring security Principal implementing UserDetails and OAuth2AuthenticatedPrincipal.
 */
@Getter
public class AuthenticatedUser implements UserDetails {

  private final UUID id;
  private final String username;
  private final String password;

  /**
   * default constructor.
   *
   * @param user a User
   */
  public AuthenticatedUser(User user) {
    id = user.getId();
    username = user.getEmail();
    password = user.getPassword();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Set.of();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
}
