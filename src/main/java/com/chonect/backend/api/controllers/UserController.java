package com.chonect.backend.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chonect.backend.api.controllers.Basic_controller.BasicController;
import com.chonect.backend.api.dto.request.UserCreateRequest;
import com.chonect.backend.api.dto.request.UserUpdateRequest;
import com.chonect.backend.api.dto.response.UserBasicResponse;
import com.chonect.backend.api.dto.response.UserResponse;
import com.chonect.backend.infrastructure.abstract_services.IUserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/user")
@AllArgsConstructor
public class UserController implements
        BasicController<UserResponse, UserBasicResponse, UserCreateRequest, UserUpdateRequest> {

    @Autowired
    private final IUserService userService;
    
    @Override
    public ResponseEntity<UserResponse> update(UserUpdateRequest request, Long id) {
        return ResponseEntity.ok(this.userService.update(request, id));
    }
    @Override
    public ResponseEntity<UserBasicResponse> insert(UserCreateRequest request) {
        return ResponseEntity.ok(this.userService.create(request));
    }


    @Override
    public ResponseEntity<UserResponse> getById(Long id) {
        return ResponseEntity.ok(this.userService.get(id));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }


}