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

    public UserEntity registerUser(String name, String email, String phone, String password) {
        UserEntity userEntity = null;
        if (userContactRepository.findByContact(email).isEmpty() &&
                userContactRepository.findByContact(phone).isEmpty() &&
                userRepository.findByName(name).isEmpty()) {
            userEntity = new UserEntity();
            userEntity.setName(name);
            userEntity.setHash(String.valueOf(System.identityHashCode(userEntity)));
            userEntity.setRegTime(LocalDateTime.now());
            userEntity.setBalance(0);
            userRepository.save(userEntity);
            if (!email.isBlank()) {
                saveUserContact(email, password, userEntity);
            }
            if (!phone.isBlank()) {
                saveUserContact(phone, password, userEntity);
            }
        }
        return userEntity;
    }

    private void saveUserContact(String contact, String password, UserEntity userEntity) {
        UserContactEntity userContact = new UserContactEntity();
        userContact.setContact(contact);
        if (contact.contains("@")) {
            userContact.setType(ContactType.EMAIL);
        } else {
            userContact.setType(ContactType.PHONE);
        }
        userContact.setCode(password);
        userContact.setUser(userEntity);
        userContactRepository.save(userContact);
    }
}
