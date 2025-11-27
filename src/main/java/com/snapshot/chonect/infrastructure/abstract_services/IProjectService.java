package com.snapshot.chonect.infrastructure.abstract_services;

import com.snapshot.chonect.domain.models.ProjectEntity;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.CreateService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.DeleteService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetAllService;
import com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services.GetByIdService;
import java.util.UUID;

public interface IProjectService extends
        CreateService<ProjectEntity, ProjectEntity>,
        GetByIdService<ProjectEntity, UUID>,
        GetAllService<ProjectEntity>,
        DeleteService<UUID> {

}