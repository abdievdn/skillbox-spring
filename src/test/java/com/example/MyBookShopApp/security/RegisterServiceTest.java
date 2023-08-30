package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RegisterServiceTest {

    private final RegisterService registerService;

    private UserEntity user;
    private UserContactEntity userContactEmail, userContactPhone;
    private String name, email, phone;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserContactRepository contactRepository;

    @Autowired
    public RegisterServiceTest(RegisterService registerService) {
        this.registerService = registerService;
    }

    @BeforeEach
    void setUp() {
        name = "Tester";
        email = "test@mail.org";
        phone = "+7889877845";
        user = UserEntity.builder()
                .id(1)
                .name(name)
                .hash(String.valueOf(System.identityHashCode(user)))
                .build();
        userContactEmail = UserContactEntity.builder()
                .id(1)
                .user(user)
                .contact(email)
                .type(ContactType.MAIL)
                .build();
        userContactPhone = UserContactEntity.builder()
                .id(2)
                .user(user)
                .contact(phone)
                .type(ContactType.PHONE)
                .build();
        user.setContacts(List.of(userContactEmail, userContactPhone));
    }

    @AfterEach
    void tearDown() {
        user = null;
        userContactEmail = null;
        userContactPhone = null;
        name = email = phone = null;
    }

    @Test
    void registerUser() {
        UserEntity userEntity = registerService.registerUser(name, email, phone);
        assertTrue(CoreMatchers.is(user.getName()).matches(userEntity.getName()));
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
        Mockito.verify(contactRepository, Mockito.times(2)).save(Mockito.any(UserContactEntity.class));
    }

    @Test
    void registerUserFail() {
        Mockito.doReturn(Optional.of(user))
                .when(userRepository)
                .findByName(name);
        registerService.registerUser(name, email, phone);
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(UserEntity.class));
        Mockito.verify(contactRepository, Mockito.times(0)).save(Mockito.any(UserContactEntity.class));
    }
}