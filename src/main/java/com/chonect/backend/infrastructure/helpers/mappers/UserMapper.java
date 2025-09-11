package com.chonect.backend.infrastructure.helpers.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.chonect.backend.api.dto.auth.AuthenticationRequest;
import com.chonect.backend.api.dto.request.UserCreateRequest;
import com.chonect.backend.api.dto.request.UserUpdateRequest;
import com.chonect.backend.api.dto.response.UserBasicResponse;
import com.chonect.backend.api.dto.response.UserResponse;
import com.chonect.backend.domain.entities.UserEntity;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface UserMapper {
    // Manual implementation to avoid Eclipse JDT APT NPE when processing Java records
    // Equivalent to: id ignored, role ignored, email <- username, fullName <- username
    default UserEntity toUserCreateEntity(AuthenticationRequest request) {
        if (request == null) {
            return null;
        }
        return UserEntity.builder()
                .username(request.username())
                .password(request.password())
                .email(request.username())
                .fullName(request.username())
                .build();
    }
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "birthDate", source = "birthDate")
    UserEntity toUserCreateEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    UserEntity toUserUpdateEntity(UserUpdateRequest request, @MappingTarget UserEntity user);

    UserResponse toUserResponse(UserEntity userEntity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    UserBasicResponse toUserBasicResponse(UserEntity userEntity);
}