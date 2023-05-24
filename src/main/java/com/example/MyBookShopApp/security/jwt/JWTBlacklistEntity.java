package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "jwt_blacklist")
public class JWTBlacklistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "jwt_value")
    private String jwtValue;

    @ManyToOne
    @JoinColumn(name = "user_contact_id", columnDefinition = "INT NOT NULL")
    private UserContactEntity userContact;
}
