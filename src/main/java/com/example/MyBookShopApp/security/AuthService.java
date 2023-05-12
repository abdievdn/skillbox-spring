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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
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
        String jwtToken = jwtUtil.generateToken(userDetails.getUsername());
        return new JWTAuthDto(jwtToken);
    }

    public CurrentUserDto getCurrentUser(Principal principal) {
        if (principal != null) {
            String name = "";
            String email = "";
            String phone = "";
            if (userContactRepository.findByContact(principal.getName()).isPresent()) {
                BookShopUserDetails userDetails = (BookShopUserDetails) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();
                UserEntity user = userDetails.getUserContactEntity().getUser();
                name = user.getName();
                for (UserContactEntity contact : user.getContacts()) {
                    if (contact.getType().equals(ContactType.EMAIL)) {
                        email = contact.getContact();
                    } else {
                        phone = contact.getContact();
                    }
                }
                return new CurrentUserDto(name, email, phone);
            } else {
                OAuth2AuthenticationToken userDetails = (OAuth2AuthenticationToken) principal;
                name = userDetails.getPrincipal().getAttribute("name");
                email = userDetails.getPrincipal().getAttribute("email");
                return new CurrentUserDto(name, email, "");
            }
        } else {
            return null;
        }

    }
}
