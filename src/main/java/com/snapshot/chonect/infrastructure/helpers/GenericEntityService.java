package com.snapshot.chonect.infrastructure.helpers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.utils.exceptions.BadRequestException;
import com.snapshot.chonect.utils.messages.ErrorMessages;

@Service
public class GenericEntityService<T, ID> {
    public T find(JpaRepository<T, ID> repository, ID id, String entityName) {
        return repository.findById(id).orElseThrow(() -> new BadRequestException(ErrorMessages.nameNotFound(entityName)));
    }
}