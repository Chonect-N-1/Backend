package com.snapshot.chonect.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapshot.chonect.api.controllers.Basic_controller.BasicController;
import com.snapshot.chonect.api.dto.request.UserRequest;
import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.infrastructure.services.UserServices;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
public class UserController implements 
    BasicController<UserResponse, UserRequest, UserUpdateRequest>
    {
    
    @Autowired
    private final UserServices userServices;
    
    @Override
    public ResponseEntity<Void> delete(Long id) {
        this.userServices.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserResponse> getById(Long id) {
        return ResponseEntity.ok(this.userServices.getById(id));
    }

    @Override
    public ResponseEntity<UserResponse> insert(UserRequest request) {
        return ResponseEntity.ok(this.userServices.create(request));
    }

    @Override
    public ResponseEntity<UserResponse> update(UserUpdateRequest request, Long id) {
        return ResponseEntity.ok(this.userServices.update(request, id));
    }
}
