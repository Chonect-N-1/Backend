package com.ejemplo.demo.infrastructure.abstract_services;

import com.ejemplo.demo.api.dto.request.UserCreateRequest;
import com.ejemplo.demo.api.dto.request.UserUpdateRequest;
import com.ejemplo.demo.api.dto.response.UserBasicResponse;
import com.ejemplo.demo.api.dto.response.UserResponse;
import com.ejemplo.demo.infrastructure.abstract_services.basic_abstract_services.BasicCrudService;

public interface IUserService extends
                BasicCrudService<UserCreateRequest, UserUpdateRequest, UserResponse, UserBasicResponse, Long> {

}