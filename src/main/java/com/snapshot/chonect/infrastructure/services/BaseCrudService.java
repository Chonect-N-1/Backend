package com.snapshot.chonect.infrastructure.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;
import com.snapshot.chonect.infrastructure.helpers.SupportService;
import com.snapshot.chonect.utils.exceptions.EntityCreationException;
import com.snapshot.chonect.utils.exceptions.EntityDeletionException;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;

import java.util.List;

public abstract class BaseCrudService<T, I, R extends JpaRepository<T, I>> {

    protected final R repository;
    protected final SupportService<T, I> supportService;

    protected BaseCrudService(R repository, SupportService<T, I> supportService) {
        this.repository = repository;
        this.supportService = supportService;
    }

    @Transactional
    public T create(@NonNull T request) {
        try {
            return repository.save(request);
        } catch (Exception e) {
            throw new EntityCreationException("Error creando " + getEntityName() + ": " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public T getById(@NonNull I id) {
        return supportService.findById(repository, id, getEntityName());
    }

    @Transactional(readOnly = true)
    public List<T> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void delete(@NonNull I id) {
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
