package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserContactRepository contactRepository;

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
            UserContactEntity userContactEntity = contactRepository.findByContact(principal.getName()).orElseThrow();
            UserEntity user = userContactEntity.getUser();
            String email = "";
            String phone = "";
            for (UserContactEntity contact : user.getContacts()) {
                if (contact.getType().equals(ContactType.EMAIL)) {
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
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
            userRepository.save(user);
        } else {
            resultDto.setError("Неверное имя пользователя");
            return resultDto;
        }
        if (userDto.getMail() != null && !userDto.getMail().isEmpty()) {
            UserContactEntity mail = contactRepository.findByContact(userDto.getMail()).orElse(null);
            if (mail != null) {
                deleteOldContact(user);
                saveNewContact(user, mail);
            }
        }
        if (userDto.getPhone() != null && !userDto.getPhone().isEmpty()) {
            UserContactEntity phone = contactRepository.findByContact(userDto.getMail()).orElse(null);
            if (phone != null) {
                deleteOldContact(user);
                saveNewContact(user, phone);
            }
        }
        resultDto.setValue("Данные успешно изменены.");
        resultDto.setResult(true);
        return resultDto;
    }

    private void saveNewContact(UserEntity user, UserContactEntity userContact) {
            userContact.setUser(user);
            userContact.setApproved((short) 1);
            log.info("try to save new contact " + userContact.getUser().getName());
            contactRepository.save(userContact);
        }

    private void deleteOldContact(UserEntity user) {
        log.info("try to delete old contact " + user.getName());
        contactRepository.findByUser(user).ifPresent(contactRepository::delete);
    }
}
