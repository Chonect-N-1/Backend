package com.snapshot.chonect.infrastructure.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

import java.util.List;

public abstract class BaseCrudService<Entity, Repository extends JpaRepository<Entity, Long>> {

    protected final Repository repository;
    protected final SupportService<Entity> supportService;

    public BaseCrudService(Repository repository, SupportService<Entity> supportService) {
        this.repository = repository;
        this.supportService = supportService;
    }

    @Transactional
    public Entity create(Entity request) {
        try {
            return repository.save(request);
        } catch (Exception e) {
            throw new RuntimeException("Error creando " + getEntityName() + ": " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Entity getById(Long id) {
        return supportService.findById(repository, id, getEntityName());
    }

    @Transactional(readOnly = true)
    public List<Entity> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        try {
            if (!repository.existsById(id)) {
                throw new RuntimeException(getEntityName() + " no encontrado: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando " + getEntityName() + ": " + e.getMessage(), e);
        }
    }

    protected abstract String getEntityName();
}
