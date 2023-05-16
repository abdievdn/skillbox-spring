package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByContactsEquals(UserContactEntity contact);
}
