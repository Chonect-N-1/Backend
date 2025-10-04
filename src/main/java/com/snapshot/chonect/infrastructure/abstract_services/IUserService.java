package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.api.dto.request.UserRequest;
import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.CreateService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.DeleteService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.UpdateService;

public interface IUserService extends
        CreateService<UserRequest, UserResponse>,
        GetByIdService<UserResponse, Long>,
        UpdateService<UserResponse, UserUpdateRequest, Long>,
        DeleteService<Long> {
                //perdona carlos por cambiar esto, entiendo el punto pero era ilegible
}