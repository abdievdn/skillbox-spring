package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookShopUserDetailsService implements UserDetailsService {

    private final UserContactRepository userContactRepository;

    @Override
    public UserDetails loadUserByUsername(String contact) throws UsernameNotFoundException {
        UserContactEntity userContactEntity = userContactRepository.findByContact(contact).orElseThrow();
        if (userContactEntity != null) {
            return new BookShopUserDetails(userContactEntity);
        } else {
            throw new UsernameNotFoundException("user not found!");
        }
    }
}
