package com.chonect.backend.infrastructure.helpers.mappers;

import com.chonect.backend.api.dto.request.UserCreateRequest;
import com.chonect.backend.api.dto.request.UserUpdateRequest;
import com.chonect.backend.api.dto.response.UserBasicResponse;
import com.chonect.backend.api.dto.response.UserResponse;
import com.chonect.backend.domain.entities.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-13T20:12:18-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

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

        user.setUsername( request.getUsername() );
        user.setPassword( request.getPassword() );
        user.setEmail( request.getEmail() );
        user.setFullName( request.getFullName() );
        user.setRole( request.getRole() );
        user.setBirthDate( request.getBirthDate() );

        return user;
    }

    @Override
    public UserResponse toUserResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder<?, ?> userResponse = UserResponse.builder();

        userResponse.id( userEntity.getId() );
        userResponse.username( userEntity.getUsername() );
        userResponse.email( userEntity.getEmail() );
        userResponse.role( userEntity.getRole() );

        return userResponse.build();
    }

    @Override
    public UserBasicResponse toUserBasicResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserBasicResponse.UserBasicResponseBuilder<?, ?> userBasicResponse = UserBasicResponse.builder();

        userBasicResponse.id( userEntity.getId() );
        userBasicResponse.username( userEntity.getUsername() );
        userBasicResponse.email( userEntity.getEmail() );
        userBasicResponse.role( userEntity.getRole() );

        return userBasicResponse.build();
    }
}
