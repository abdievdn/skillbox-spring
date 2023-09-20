package com.example.MyBookShopApp.mappers;

import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "mail", source = "user.contacts", qualifiedByName = "GetMail")
    @Mapping(target = "phone",  source = "user.contacts", qualifiedByName = "GetPhone")
    UserDto toUserDto(UserEntity user);

    @Named("GetMail")
    default String mapMail(List<UserContactEntity> contacts) {
        return getContact(contacts, ContactType.MAIL);
    }

    @Named("GetPhone")
    default String mapPhone(List<UserContactEntity> contacts) {
        return getContact(contacts, ContactType.PHONE);
    }
    private String getContact(List<UserContactEntity> contacts, ContactType contactType) {
        return contacts
                .stream()
                .filter(c -> c.getType().equals(contactType))
                .findFirst()
                .orElse(UserContactEntity.builder()
                        .contact("")
                        .build())
                .getContact();
    }
}
