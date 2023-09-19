package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import com.example.MyBookShopApp.security.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserContactRepository contactRepository;
    private final AuthService authService;

    private UserEntity getUserByContact(UserContactEntity contact) {
        if (contact != null) {
            return contact.getUser();
        } else {
            return null;
        }
    }

    public UserEntity getCurrentUserByPrincipal(Principal principal) {
        return principal != null ?
                userRepository.findById(Integer.valueOf(principal.getName()))
                        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден!")) : null;
    }

    @ServiceProcessTrackable
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
            UserEntity user = getCurrentUserByPrincipal(principal);
            String email = "";
            String phone = "";
            for (UserContactEntity contact : user.getContacts()) {
                if (contact.getType().equals(ContactType.MAIL)) {
                    email = contact.getContact();
                } else {
                    phone = contact.getContact();
                }
            }
            return UserDto.builder()
                    .name(user.getName())
                    .balance(user.getBalance())
                    .mail(email)
                    .phone(phone)
                    .build();
        } else {
            return null;
        }
    }

    public ResultDto changeProfileData(UserDto userDto, Principal principal) {
        ResultDto resultDto = new ResultDto("");
        UserEntity user = getCurrentUserByPrincipal(principal);
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            if (!userDto.getName().equals(user.getName())) {
                user.setName(userDto.getName());
                userRepository.save(user);
            }
        } else {
            resultDto.setError("Неверное имя пользователя");
            return resultDto;
        }
        if (isUserChangeContact(userDto.getMail())) {
            UserContactEntity mail = authService.getUserContact(userDto.getMail());
            if (isNewContact(mail)) {
                deleteOldContact(user, ContactType.MAIL);
                saveNewContact(user, mail);
            }
        }
        if (isUserChangeContact(userDto.getPhone())) {
            UserContactEntity phone = authService.getUserContact(userDto.getPhone());
            if (isNewContact(phone)) {
                deleteOldContact(user, ContactType.PHONE);
                saveNewContact(user, phone);
            }
        }
        resultDto.setResult(true);
        return resultDto;
    }

    private boolean isNewContact(UserContactEntity contact) {
        return contact != null && contact.getApproved() == 0;
    }

    private boolean isUserChangeContact(String contact) {
        return contact != null && !contact.isEmpty();
    }

    private void saveNewContact(UserEntity user, UserContactEntity userContact) {
            userContact.setUser(user);
            userContact.setApproved((short) 1);
            contactRepository.save(userContact);
        }

    private void deleteOldContact(UserEntity user, ContactType type) {
        UserContactEntity userContact = contactRepository.findByUserAndType(user, type).orElse(null);
        if (userContact != null) {
            user.getContacts().remove(userContact);
            contactRepository.delete(userContact);
        }
    }
}
