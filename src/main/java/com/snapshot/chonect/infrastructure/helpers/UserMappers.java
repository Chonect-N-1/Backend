package com.snapshot.chonect.infrastructure.helpers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.snapshot.chonect.api.dto.request.UserRequest;
import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.domain.models.UserEntity;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface UserMappers {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "birthDate", source = "birthDate")
    UserEntity userRequestToUserEntity(UserRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "customerType", source = "customerType")
    @Mapping(target = "termsVersion", source = "termsVersion")
    @Mapping(target = "birthDate", source = "birthDate")
    UserResponse userEntityToUserResponse(UserEntity userEntity);

    // si alguien ve esto quiero que sepan que no quiero ser muy mamon re haciendo el codigo,
    // solo que encontre una manera mas facil de hacerlo

    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "role", source = "role")
    UserEntity requestUpdateToEntity(UserUpdateRequest userRequest);
}
