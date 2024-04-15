package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.repository.interfaces.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.Serial;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@AllArgsConstructor
@Builder
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User byLogin = repository.findUserByUsername(username);
        if (byLogin == null) {
            throw new UsernameNotFoundException("Username didn't found" + username);
        }

        return new UserDetails() {
            @Serial
            private static final long serialVersionUID = 1892212061235632245L;

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return byLogin.getRole().getPermissions().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                        .collect(Collectors.toList());
            }

            @Override
            public String getPassword() {
                return byLogin.getPassword();
            }

            @Override
            public String getUsername() {
                return byLogin.getUsername();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return !byLogin.getIsDeleted();
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return byLogin.getIsActive();
            }
        };
    }
}
