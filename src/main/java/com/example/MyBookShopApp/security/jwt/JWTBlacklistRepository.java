package com.example.MyBookShopApp.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JWTBlacklistRepository extends JpaRepository<JWTBlacklistEntity, Integer> {

    Boolean existsByJwtValue(String jwtValue);
}
