package com.haufe.beercatalogue.security;

import com.haufe.beercatalogue.domain.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<SimpleGrantedAuthority> authorities;
    private final User user;

    private UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        this.user = user;
    }

    public static UserPrincipal from(User user) {
        return new UserPrincipal(user);
    }
}
