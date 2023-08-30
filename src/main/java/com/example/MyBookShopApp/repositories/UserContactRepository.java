package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserContactRepository extends JpaRepository<UserContactEntity, Integer> {

    Optional<UserContactEntity> findByContact(String contact);
    Optional<UserContactEntity> findByUserAndType(UserEntity user, ContactType type);
}
