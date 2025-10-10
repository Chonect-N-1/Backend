package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.domain.models.ConnectionEntity;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.CreateService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.DeleteService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetAllService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;

public interface IConnectionService extends 
    CreateService<ConnectionEntity, ConnectionEntity>,
    GetByIdService<ConnectionEntity, Long>,
    GetAllService<ConnectionEntity>,
    DeleteService<Long>
{

}
