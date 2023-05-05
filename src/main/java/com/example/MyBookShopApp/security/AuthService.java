package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.CurrentUserDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.struct.enums.ContactType;
import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import com.example.MyBookShopApp.security.jwt.JWTAuthDto;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
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
public class AuthService {

    private final UserContactRepository userContactRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookShopUserDetailsService bookShopUserDetailsService;
    private final JWTUtil jwtUtil;

    public ResultDto checkContact(ContactConfirmationDto confirmationDto) {
        if (userContactRepository.findByContact(confirmationDto.getContact()).isPresent()) {
            return new ResultDto("Такой контакт уже зарегистрирован!");
        } else {
            return new ResultDto(true);
        }
    }

    public void registerNewUser(AuthUserDto authUserDto) {
        if (userContactRepository.findByContact(authUserDto.getEmail()).isEmpty() &&
                userContactRepository.findByContact(authUserDto.getPhone()).isEmpty()) {
            UserEntity userEntity = new UserEntity();
            userEntity.setName(authUserDto.getName());
            userEntity.setHash(String.valueOf(System.identityHashCode(userEntity)));
            userEntity.setRegTime(LocalDateTime.now());
            userRepository.save(userEntity);
            saveUserContact(authUserDto.getEmail(), authUserDto.getPassword(), userEntity);
            saveUserContact(authUserDto.getPhone(), authUserDto.getPassword(), userEntity);
        }
    }

    private void saveUserContact(String contact, String password, UserEntity userEntity) {
        UserContactEntity userContact = new UserContactEntity();
        userContact.setContact(contact);
        if (contact.contains("@")) {
            userContact.setType(ContactType.EMAIL);
        } else {
            userContact.setType(ContactType.PHONE);
        }
        userContact.setCode(passwordEncoder.encode(password));
        userContact.setUser(userEntity);
        userContactRepository.save(userContact);
    }

    public JWTAuthDto jwtLogin(ContactConfirmationDto payload) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        BookShopUserDetails userDetails =
                (BookShopUserDetails) bookShopUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        return new JWTAuthDto(jwtToken);
    }

    public CurrentUserDto getCurrentUser(Principal principal) {
        if (principal != null) {
            BookShopUserDetails userDetails =
                    (BookShopUserDetails) SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal();
            UserContactEntity userContactEntity = userDetails.getUserContactEntity();
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
            CurrentUserDto currentUserDto = new CurrentUserDto(user.getName(), email, phone);
            return currentUserDto;
        } else {
            return null;
        }

    }
}
