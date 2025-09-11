package com.chonect.backend.infrastructure.helpers.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.chonect.backend.api.dto.request.UserCreateRequest;
import com.chonect.backend.api.dto.request.UserUpdateRequest;
import com.chonect.backend.api.dto.response.UserBasicResponse;
import com.chonect.backend.api.dto.response.UserResponse;
import com.chonect.backend.domain.entities.UserEntity;
import com.chonect.backend.domain.entities.repositories.UserRepository;
import com.chonect.backend.infrastructure.abstract_services.IUserService;
import com.chonect.backend.infrastructure.helpers.generic_methods.GenericEntityService;
import com.chonect.backend.infrastructure.helpers.mappers.UserMapper;
import com.chonect.backend.utils.enums.Role;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final GenericEntityService<UserEntity, Long> genericEntityService;

    @Override
    public UserBasicResponse create(UserCreateRequest request) {
        UserEntity userEntity = this.userMapper.toUserCreateEntity(request);
        userEntity.setRole(Role.CUSTOMER);
        return this.userMapper.toUserBasicResponse(this.userRepository.save(userEntity));
    }

    @Override
    public UserResponse get(Long id) {
        UserEntity userEntity = this.genericEntityService.find(userRepository, id, "UserEntity");
        return this.userMapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse update(UserUpdateRequest request, Long id) {
        UserEntity userEntity = this.genericEntityService.find(userRepository, id, "UserEntity");
        userEntity = this.userMapper.toUserUpdateEntity(request, userEntity);
        return this.userMapper.toUserResponse(this.userRepository.save(userEntity));

    }

    @Override
    public void delete(Long id) {
        UserEntity userEntity = this.genericEntityService.find(userRepository, id, "UserEntity");
        this.userRepository.delete(userEntity);

    }
}