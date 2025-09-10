package com.ejemplo.demo.infrastructure.helpers.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.ejemplo.demo.api.dto.auth.AuthenticationRequest;
import com.ejemplo.demo.api.dto.request.UserCreateRequest;
import com.ejemplo.demo.api.dto.request.UserUpdateRequest;
import com.ejemplo.demo.api.dto.response.UserBasicResponse;
import com.ejemplo.demo.api.dto.response.UserResponse;
import com.ejemplo.demo.domain.entities.UserEntity;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "email", source = "username")
    @Mapping(target = "fullName", source = "username")
    UserEntity toUserCreateEntity(AuthenticationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserEntity toUserCreateEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    UserEntity toUserUpdateEntity(UserUpdateRequest request, @MappingTarget UserEntity user);

    @InheritInverseConfiguration
    UserResponse toUserResponse(UserEntity userEntity);

    @InheritInverseConfiguration
    UserBasicResponse toUserBasicResponse(UserEntity userEntity);
}