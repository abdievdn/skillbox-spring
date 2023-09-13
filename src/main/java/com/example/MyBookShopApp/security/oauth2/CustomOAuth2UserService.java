package com.example.MyBookShopApp.security.oauth2;

import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.security.BookShopUserService;
import com.example.MyBookShopApp.security.RegisterService;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.services.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final RegisterService registerService;
    private final UserContactRepository contactRepository;
    private final JWTUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        return new CustomOAuth2User(user);
    }

    public void processOAuth2PostLogin(HttpServletRequest request, HttpServletResponse response, CustomOAuth2User oAuth2User) {
        registerService.registerUser(oAuth2User.getUsername(), oAuth2User.getName(), "");
        UserContactEntity contactEntity = contactRepository.findByContact(oAuth2User.getName()).orElseThrow();
        BookShopUserService.contactType = ContactType.MAIL;
        CookieUtil.deleteCookieByName(request, response,"JSESSIONID");
        CookieUtil.addCookieByName(response, "token",
                jwtUtil.generateToken(String.valueOf(contactEntity.getUser().getId())));
    }
}
