package com.example.MyBookShopApp.security;

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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterServiceTest {

    private final RegisterService registerService;

    String name, email, phone, password;
    UserEntity user = new UserEntity();

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private UserContactRepository contactRepositoryMock;

    @Autowired
    public RegisterServiceTest(RegisterService registerService) {
        this.registerService = registerService;
    }

    @BeforeEach
    void setUp() {
        name = "Tester";
        email = "test@mail.org";
        phone = "+7889877845";
        password = "123123";
        user.setName(name);
        user.setHash(String.valueOf(System.identityHashCode(user)));
        user.setId(1);
        user.setRegTime(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        name = email = phone = password = null;
    }

    @Test
    void registerUser() {
        registerService.registerUser(name, email, phone, password);
//        assertTrue(CoreMatchers.is(user.getName()).matches(name));
        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any(UserEntity.class));
        Mockito.verify(contactRepositoryMock, Mockito.times(2)).save(Mockito.any(UserContactEntity.class));
    }

    @Test
    void registerUserFail() {
        Mockito.doReturn(Optional.of(user))
                .when(userRepositoryMock)
                .findByName(name);
        registerService.registerUser(name, email, phone, password);
        Mockito.verify(userRepositoryMock, Mockito.times(0)).save(Mockito.any(UserEntity.class));
        Mockito.verify(contactRepositoryMock, Mockito.times(0)).save(Mockito.any(UserContactEntity.class));
    }
}