package com.snapshot.chonect.infrastructure.helpers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.messages.ErrorMessages;

@Service
public class SupportService<Entity> {
    public Entity findById(JpaRepository<Entity, Long> repository, Long id, String name) {
        return repository.findById(id)
                .orElseThrow(() -> new IdNotFoundException(ErrorMessages.idNotFound(name)));
    }
}