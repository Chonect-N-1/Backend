package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.domain.models.ElementEntity;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.CreateService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.DeleteService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetAllService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;

public interface IElementService extends 
    CreateService<ElementEntity, ElementEntity>,
    GetByIdService<ElementEntity, Long>,
    GetAllService<ElementEntity>,
    DeleteService<Long>
{
}
