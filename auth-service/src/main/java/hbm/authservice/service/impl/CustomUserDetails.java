package hbm.authservice.service.impl;

import hbm.authservice.entity.User;
import hbm.authservice.repository.RoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final List<String> roles;
    private final String email;
    private final Long userId;

    // Constructor cho luồng Login (Dùng User Entity đầy đủ)
    public CustomUserDetails(User user, List<String> roles) {
        this.user = user;
        this.roles = roles;
        this.email = user.getEmail(); // Lấy từ entity
        this.userId = user.getId();   // Lấy từ entity
    }

    // Constructor MỚI cho luồng Gateway/Header
    public CustomUserDetails(String email, Long userId, List<String> roles) {
        this.user = null;
        this.email = email;
        this.userId = userId;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // chuyển list role -> list authority
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPasswd();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user != null && user.getStatus() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user != null && user.getStatus() == 1;
    }

}
