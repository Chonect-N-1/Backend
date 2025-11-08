package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.UpdateService;
import java.util.UUID;

public interface IUserService extends
        GetByIdService<UserResponse, UUID>,
        UpdateService<UserResponse, UserUpdateRequest, UUID> {
                //perdona carlos por cambiar esto, entiendo el punto pero era ilegible
}
