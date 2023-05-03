package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.CurrentUserDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserContactRepository userContactRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ResultDto checkContact(ContactConfirmationDto confirmationDto) {
        if (userContactRepository.findByContact(confirmationDto.getContact()).isPresent()) {
            return new ResultDto("Такой контакт уже зарегистрирован!");
        } else {
            return new ResultDto(true);
        }
    }
    public void registerNewUser(RegistrationForm registrationForm) {
        if (userContactRepository.findByContact(registrationForm.getEmail()).isEmpty() &&
                userContactRepository.findByContact(registrationForm.getPhone()).isEmpty()) {
            UserEntity userEntity = new UserEntity();
            userEntity.setName(registrationForm.getName());
            userEntity.setHash(String.valueOf(System.identityHashCode(userEntity)));
            userEntity.setRegTime(LocalDateTime.now());
            userRepository.save(userEntity);
            saveUserContact(registrationForm.getEmail(), registrationForm.getPassword(), userEntity);
            saveUserContact(registrationForm.getPhone(), registrationForm.getPassword(), userEntity);
        }
    }

    private void saveUserContact(String contact, String password, UserEntity userEntity) {
        UserContactEntity userContact = new UserContactEntity();
        userContact.setContact(contact);
        userContact.setCode(passwordEncoder.encode(password));
        userContact.setUser(userEntity);
        userContactRepository.save(userContact);
    }

    public ResultDto login(ContactConfirmationDto payload) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResultDto resultDto = new ResultDto(true);
        return resultDto;
    }

    public CurrentUserDto getCurrentUser(Principal principal) {
        if (principal != null) {
            BookShopUserDetails userDetails =
                    (BookShopUserDetails) SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal();
            UserContactEntity userContactEntity = userDetails.getUserContactEntity();
            CurrentUserDto currentUserDto =
                    new CurrentUserDto(
                            userContactEntity.getUser().getName(),
                            userContactEntity.getContact(),
                            "");
            return currentUserDto;
        } else {
            return null;
        }

    }
}
