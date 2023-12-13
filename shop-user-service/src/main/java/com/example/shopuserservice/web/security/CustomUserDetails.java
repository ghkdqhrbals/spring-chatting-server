package com.example.shopuserservice.web.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;

/**
 * {@link CustomUserDetails} class is used to store user information which is returned by UserDetailsService.
 * This class is used by Spring Security internally for authentication and authorization purposes.
 * This class must implement UserDetails interface.
 */
@Getter
@Setter
@Slf4j
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    private List<String> permissions = new ArrayList<>();

    public CustomUserDetails() {
    }

    public CustomUserDetails(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Add permissions to the user based on the role.
     * @return Collection<? extends GrantedAuthority>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        permissions.stream().forEach(permission -> {
            authorities.add(new SimpleGrantedAuthority(permission));
        });
        authorities.forEach(a->{
            log.info("getAuthorities={}",a.getAuthority());
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}