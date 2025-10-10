package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.UpdateService;

public interface IUserService extends
        GetByIdService<UserResponse, Long>,
        UpdateService<UserResponse, UserUpdateRequest, Long> {
                //perdona carlos por cambiar esto, entiendo el punto pero era ilegible
}