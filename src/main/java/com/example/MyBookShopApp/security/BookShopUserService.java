package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookShopUserService implements UserDetailsService {

    private final UserContactRepository userContactRepository;

    @Override
    public UserDetails loadUserByUsername(String contact) throws UsernameNotFoundException {
        UserContactEntity userContactEntity = userContactRepository.findByContact(contact).orElse(null);
        return new BookShopUser(userContactEntity);
    }
}
