package com.snapshot.chonect.infrastructure.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.api.dto.request.UserRequest;
import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.domain.repositories.UserRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IUserService;
import com.snapshot.chonect.infrastructure.helpers.GenericEntityService;
import com.snapshot.chonect.infrastructure.helpers.UserMappers;
import com.snapshot.chonect.utils.enums.Role;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServices implements IUserService {

    @Autowired
    private final UserMappers userMapper;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GenericEntityService<UserEntity, Long> genericEntityService;


    @Override
    public UserResponse create(UserRequest request) {
        UserEntity userEntity = this.userMapper.userRequestToUserEntity(request);
        userEntity.setRole(Role.CUSTOMER);
        return this.userMapper.userEntityToUserResponse(this.userRepository.save(userEntity));
    }

    @Override
    public UserResponse getById(Long id) {
        UserEntity userEntity = this.genericEntityService.find(userRepository, id, "UserEntity");
        return this.userMapper.userEntityToUserResponse(userEntity);
    }

    @Override
    public UserResponse update(UserUpdateRequest request, Long id) {
        // dejo este codigo porque lo mas probable es que lo necesite en un futuro cercano
        // UserEntity userEntity = this.genericEntityService.find(userRepository, id, "UserEntity");
        UserEntity userUpdate = this.userMapper.requestUpdateToEntity(request);
        userUpdate.setId(id);
        return this.userMapper.userEntityToUserResponse(this.userRepository.save(userUpdate));
    }

    @Override
    public void delete(Long id) {
        UserEntity user = this.genericEntityService.find(userRepository, id, "UserEntity");
        this.userRepository.delete(user);
    }

}
