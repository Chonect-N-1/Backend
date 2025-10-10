package com.snapshot.chonect.infrastructure.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.domain.repositories.UserRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IUserService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;
import com.snapshot.chonect.infrastructure.helpers.UserMappers;

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
    private final SupportService<UserEntity> supportService;

    @Override
    public UserResponse getById(Long id) {
        UserEntity userEntity = this.supportService.findById(userRepository, id, "UserEntity");
        return this.userMapper.userEntityToUserResponse(userEntity);
    }

    @Override
    public UserResponse update(UserUpdateRequest request, Long id) {
        // dejo este codigo porque lo mas probable es que lo necesite en un futuro cercano
        // UserEntity userEntity = this.supportService.find(userRepository, id, "UserEntity");
        UserEntity userUpdate = this.userMapper.requestUpdateToEntity(request);
        userUpdate.setId(id);
        return this.userMapper.userEntityToUserResponse(this.userRepository.save(userUpdate));
    }

    public UserEntity getByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }
}
