package com.snapshot.chonect.infrastructure.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.snapshot.chonect.infrastructure.helpers.SupportService;
import com.snapshot.chonect.utils.exceptions.EntityCreationException;
import com.snapshot.chonect.utils.exceptions.EntityDeletionException;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;

import java.util.List;

public abstract class BaseCrudService<T, R extends JpaRepository<T, Long>> {

    protected final R repository;
    protected final SupportService<T> supportService;

    protected BaseCrudService(R repository, SupportService<T> supportService) {
        this.repository = repository;
        this.supportService = supportService;
    }

    @Transactional
    public T create(T request) {
        try {
            return repository.save(request);
        } catch (Exception e) {
            throw new EntityCreationException("Error creando " + getEntityName() + ": " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public T getById(Long id) {
        return supportService.findById(repository, id, getEntityName());
    }

    @Transactional(readOnly = true)
    public List<T> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        try {
            if (!repository.existsById(id)) {
                throw new IdNotFoundException(getEntityName() + " no encontrado: " + id);
            }
            repository.deleteById(id);
        } catch (IdNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new EntityDeletionException("Error eliminando " + getEntityName() + ": " + e.getMessage(), e);
        }
    }

    protected abstract String getEntityName();
}
