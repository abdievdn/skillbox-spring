package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import com.example.MyBookShopApp.security.BookShopUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserContactRepository contactRepository;
    private final UserRepository userRepository;

    private UserEntity getUserByContact(UserContactEntity contact) {
        if (contact != null) {
            return contact.getUser();
        } else {
            return null;
        }
    }

    private UserContactEntity getUserContactByPrincipal(Principal principal) {
        return contactRepository.findByContact(principal.getName()).orElse(null);
    }

    public UserEntity getCurrentUserByPrincipal(Principal principal) {
        return getUserByContact(getUserContactByPrincipal(principal));
    }

    public UserEntity getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String contact = SecurityContextHolder.getContext().getAuthentication().getName();
            return getUserByContact(contactRepository.findByContact(contact).orElse(null));
        } else {
            return null;
        }
    }

    public UserDto getCurrentUserDto(Principal principal) {
        if (principal != null) {
            String name = "";
            String email = "";
            String phone = "";
            UserContactEntity userContactEntity = contactRepository.findByContact(principal.getName()).orElse(null);
            UserEntity user = userContactEntity.getUser();
            if (user != null) {
                name = user.getName();
                for (UserContactEntity contact : user.getContacts()) {
                    if (contact.getType().equals(ContactType.EMAIL)) {
                        email = contact.getContact();
                    } else {
                        phone = contact.getContact();
                    }
                }
            }
            return new UserDto(name, email, phone);
        } else {
            return null;
        }
    }

}
