package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserContactRepository userContactRepository;
    private final UserRepository userRepository;

    public UserEntity registerUser(String name, String email, String phone) {
        UserEntity userEntity = null;
        if (userRepository.findByName(name).isEmpty()) {
            userEntity = UserEntity.builder()
                    .name(name)
                    .regTime(LocalDateTime.now())
                    .balance(0)
                    .build();
            userEntity.setHash(String.valueOf(System.identityHashCode(userEntity)));
            userRepository.save(userEntity);
            if (!email.isBlank()) {
                saveUserContact(email, userEntity, ContactType.MAIL);
            }
            if (!phone.isBlank()) {
                saveUserContact(phone, userEntity, ContactType.PHONE);
            }
        }
        return userEntity;
    }

    private void saveUserContact(String contact, UserEntity userEntity, ContactType contactType) {
        UserContactEntity userContact = userContactRepository.findByContact(contact).orElse(new UserContactEntity());
        userContact.setContact(contact);
        userContact.setApproved((short) 1);
        userContact.setUser(userEntity);
        userContact.setType(contactType);
        userContactRepository.save(userContact);
    }
}
