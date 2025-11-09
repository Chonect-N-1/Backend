package com.snapshot.chonect.infrastructure.helpers;

import java.util.Objects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.messages.ErrorMessages;

@Service
public class SupportService<T, I> {
    public T findById(JpaRepository<T, I> repository, I id, String name) {
        Objects.requireNonNull(id, "ID must not be null");
        return repository.findById(id)
                .orElseThrow(() -> new IdNotFoundException(ErrorMessages.idNotFound(name)));
    }
}
