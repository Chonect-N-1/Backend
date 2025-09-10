package com.ejemplo.demo.infrastructure.helpers.mappers;

import com.ejemplo.demo.api.dto.auth.AuthenticationRequest;
import com.ejemplo.demo.api.dto.request.UserCreateRequest;
import com.ejemplo.demo.api.dto.request.UserUpdateRequest;
import com.ejemplo.demo.api.dto.response.UserBasicResponse;
import com.ejemplo.demo.api.dto.response.UserResponse;
import com.ejemplo.demo.domain.entities.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T08:28:13-0500",
    comments = "version: 1.5.2.Final, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toUserCreateEntity(AuthenticationRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.email( request.username() );
        userEntity.fullName( request.username() );
        userEntity.password( request.password() );
        userEntity.username( request.username() );

        return userEntity.build();
    }

    @Override
    public UserEntity toUserCreateEntity(UserCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.username( request.getUsername() );
        userEntity.password( request.getPassword() );
        userEntity.email( request.getEmail() );
        userEntity.fullName( request.getFullName() );
        userEntity.birthDate( request.getBirthDate() );

        return userEntity.build();
    }

    @Override
    public UserEntity toUserUpdateEntity(UserUpdateRequest request, UserEntity user) {
        if ( request == null ) {
            return user;
        }

        user.setBirthDate( request.getBirthDate() );
        user.setEmail( request.getEmail() );
        user.setFullName( request.getFullName() );
        user.setPassword( request.getPassword() );
        user.setRole( request.getRole() );
        user.setUsername( request.getUsername() );

        return user;
    }

    @Override
    public UserResponse toUserResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder<?, ?> userResponse = UserResponse.builder();

        userResponse.email( userEntity.getEmail() );
        userResponse.id( userEntity.getId() );
        userResponse.role( userEntity.getRole() );
        userResponse.username( userEntity.getUsername() );

        return userResponse.build();
    }

    @Override
    public UserBasicResponse toUserBasicResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserBasicResponse.UserBasicResponseBuilder<?, ?> userBasicResponse = UserBasicResponse.builder();

        userBasicResponse.email( userEntity.getEmail() );
        userBasicResponse.id( userEntity.getId() );
        userBasicResponse.role( userEntity.getRole() );
        userBasicResponse.username( userEntity.getUsername() );

        return userBasicResponse.build();
    }
}
