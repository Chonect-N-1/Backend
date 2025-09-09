package com.ejemplo.demo.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejemplo.demo.api.controllers.Basic_controller.BasicController;
import com.ejemplo.demo.api.dto.request.UserCreateRequest;
import com.ejemplo.demo.api.dto.request.UserUpdateRequest;
import com.ejemplo.demo.api.dto.response.UserBasicResponse;
import com.ejemplo.demo.api.dto.response.UserResponse;
import com.ejemplo.demo.infrastructure.abstract_services.IUserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/user")
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