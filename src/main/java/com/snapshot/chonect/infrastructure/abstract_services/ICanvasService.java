package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.domain.models.CanvasEntity;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.CreateService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.DeleteService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetAllService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;
import java.util.UUID;

public interface ICanvasService extends
        CreateService<CanvasEntity, CanvasEntity>,
        GetByIdService<CanvasEntity, UUID>,
        GetAllService<CanvasEntity>,
        DeleteService<UUID> {

}
