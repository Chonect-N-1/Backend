package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.domain.models.PageEntity;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.CreateService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.DeleteService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetAllService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;
import java.util.UUID;

public interface IPageService extends
        CreateService<PageEntity, PageEntity>,
        GetByIdService<PageEntity, UUID>,
        GetAllService<PageEntity>,
        DeleteService<UUID> {

}
