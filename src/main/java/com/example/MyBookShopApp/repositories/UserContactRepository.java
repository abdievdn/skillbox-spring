package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserContactRepository extends JpaRepository<UserContactEntity, Integer> {

    Optional<UserContactEntity> findByContact(String contact);
    Boolean existsByContact(String contact);
}
