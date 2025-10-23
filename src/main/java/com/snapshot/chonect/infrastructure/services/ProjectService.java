package com.snapshot.chonect.infrastructure.services;

import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.models.ProjectEntity;
import com.snapshot.chonect.domain.repositories.ProjectRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IProjectService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

@Service
public class ProjectService extends BaseCrudService<ProjectEntity, ProjectRepository> implements IProjectService {

    public ProjectService(ProjectRepository repository, SupportService<ProjectEntity> supportService) {
        super(repository, supportService);
    }

    @Override
    protected String getEntityName() {
        return "Project";
    }
}
