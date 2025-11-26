package com.snapshot.chonect.infrastructure.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.snapshot.chonect.domain.models.ProjectEntity;
import com.snapshot.chonect.domain.repositories.ProjectRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IProjectService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

@Service
public class ProjectService extends BaseCrudService<ProjectEntity, Long, ProjectRepository> implements IProjectService {

    @PersistenceContext
    private EntityManager entityManager;

    public ProjectService(ProjectRepository repository, SupportService<ProjectEntity, Long> supportService) {
        super(repository, supportService);
    }

    @Override
    protected String getEntityName() {
        return "Project";
    }

    public boolean existsByNameAndUser(String name, com.snapshot.chonect.domain.models.UserEntity user) {
        return repository.existsByProjectNameAndUser(name, user);
    }

    public java.util.List<ProjectEntity> getAllWithPages() {
        return repository.findAllBy();
    }

    public java.util.Optional<ProjectEntity> getByIdWithPages(Long id) {
        return repository.findWithPagesById(id);
    }

    @Transactional
    public ProjectEntity update(ProjectEntity project) {
        // Flush para asegurar que los cambios se persisten correctamente
        ProjectEntity merged = entityManager.merge(project);
        entityManager.flush();
        return merged;
    }
}
