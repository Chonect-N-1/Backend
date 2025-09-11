package com.chonect.backend.infrastructure.helpers.generic_methods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.chonect.backend.utils.exceptions.IdNotFoundException;

@Service
public class GenericEntityService<T, ID> {
    public T find(JpaRepository<T, ID> repository, ID id, String entityName) {
        return repository.findById(id).orElseThrow(() -> new IdNotFoundException(entityName));
    }
}