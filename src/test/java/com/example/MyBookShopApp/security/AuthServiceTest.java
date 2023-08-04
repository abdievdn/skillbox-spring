package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Slf4j
@SpringBootTest
class AuthServiceTest {

    private final AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserContactRepository userContactRepository;

    @MockBean
    BookShopUserService bookShopUserService;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    private JWTUtil jwtUtil;

    final HttpServletResponse response = mock(HttpServletResponse.class);
    private ContactConfirmationDto contactConfirmationDto;

    private BookShopUser bookShopUser;
    private UserContactEntity userContact;
    private String token;
    private String contact;

    @Autowired
    AuthServiceTest(AuthService authService) {
        this.authService = authService;
    }

    @BeforeEach
    void setUp() {
        contact = "test@mail.org";
        String code = "111111";
        contactConfirmationDto = ContactConfirmationDto.builder()
                .contact(contact)
                .code(code)
                .build();
        token = "testJwt";
        userContact = UserContactEntity.builder()
                .id(1)
                .contact(contact)
                .code(code)
                .build();
        bookShopUser = new BookShopUser(userContact);
    }

    @AfterEach
    void tearDown() {
        contactConfirmationDto = null;
    }

    @Test
    void checkContact() {
        Mockito.doReturn(Optional.of(userContact))
                .when(userContactRepository)
                .findByContact(contactConfirmationDto.getContact());
        ResultDto resultDto = authService.checkSignupContact(contactConfirmationDto);
        assertFalse(resultDto.getResult());
    }

    @Test
    void registerNewUser() {
        UserDto userDto = UserDto.builder()
                .name("user")
                .email("test@mail.org")
                .phone("123123123")
                .build();
        authService.registerNewUser(userDto);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void login() {
        Mockito.doReturn(null)
                .when(authenticationManager)
                .authenticate(Mockito.any());
        Mockito.doReturn(bookShopUser)
                .when(bookShopUserService)
                .loadUserByUsername(Mockito.any());
        Mockito.doReturn(token)
                .when(jwtUtil)
                .generateToken(contact);
        ResultDto resultDto = authService.login(contactConfirmationDto, response);
        assertNotNull(resultDto);
        assertEquals(resultDto.getValue(), token);
    }

    @Test
    void loginFail() {
        Mockito.doReturn(null)
                .when(authenticationManager)
                .authenticate(Mockito.any());
        Mockito.doReturn(bookShopUser)
                .when(bookShopUserService)
                .loadUserByUsername(Mockito.any());
        Mockito.doReturn("anotherJwt")
                .when(jwtUtil)
                .generateToken("222222");
        ResultDto resultDto = authService.login(contactConfirmationDto, response);
        assertNotNull(resultDto);
        assertNotEquals(resultDto.getValue(), token);
    }
}