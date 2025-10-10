package com.snapshot.chonect.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapshot.chonect.api.controllers.Basic_controller.GetByIdController;
import com.snapshot.chonect.api.controllers.Basic_controller.PutController;
import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.infrastructure.services.UserServices;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/user")
@AllArgsConstructor
public class UserController implements GetByIdController<UserResponse>, PutController<UserResponse, UserUpdateRequest>
    {
    
    @Autowired
    private final UserServices userServices;
    
    @Override
    public ResponseEntity<UserResponse> getById(Long id) {
        return ResponseEntity.ok(this.userServices.getById(id));
    }

    @Override
    public ResponseEntity<UserResponse> update(UserUpdateRequest request, Long id) {
        return ResponseEntity.ok(this.userServices.update(request, id));
    }
}
