package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@RequiredArgsConstructor
public class BookShopUserDetails implements UserDetails {

    private final UserContactEntity userContactEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return userContactEntity.getCode();
    }

    @Override
    public String getUsername() {
        return userContactEntity.getContact();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
