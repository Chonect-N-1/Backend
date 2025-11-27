package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.domain.models.ConnectionEntity;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.CreateService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.DeleteService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetAllService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;
import java.util.UUID;

public interface IConnectionService extends
        CreateService<ConnectionEntity, ConnectionEntity>,
        GetByIdService<ConnectionEntity, UUID>,
        GetAllService<ConnectionEntity>,
        DeleteService<UUID> {

}
