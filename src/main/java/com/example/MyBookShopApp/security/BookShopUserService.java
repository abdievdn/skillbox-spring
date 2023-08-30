package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookShopUserService implements UserDetailsService {

    private final UserContactRepository userContactRepository;
    private final UserRepository userRepository;
    public static ContactType contactType;

    @Override
    public UserDetails loadUserByUsername(String userId) {
        UserEntity userEntity = userRepository.findById(Integer.valueOf(userId)).orElse(null);
        UserContactEntity userContactEntity = userContactRepository.findByUserAndType(userEntity, contactType).orElse(null);
        return new BookShopUser(userContactEntity);
    }
 }
