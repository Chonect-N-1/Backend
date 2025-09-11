package com.chonect.backend.infrastructure.abstract_services;

import com.chonect.backend.api.dto.request.UserCreateRequest;
import com.chonect.backend.api.dto.request.UserUpdateRequest;
import com.chonect.backend.api.dto.response.UserBasicResponse;
import com.chonect.backend.api.dto.response.UserResponse;
import com.chonect.backend.infrastructure.abstract_services.basic_abstract_services.BasicCrudService;

public interface IUserService extends
                BasicCrudService<UserCreateRequest, UserUpdateRequest, UserResponse, UserBasicResponse, Long> {

}