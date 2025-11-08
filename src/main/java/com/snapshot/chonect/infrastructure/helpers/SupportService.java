package com.snapshot.chonect.infrastructure.helpers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.messages.ErrorMessages;

@Service
public class SupportService<T, I> {
    public T findById(JpaRepository<T, I> repository, @NonNull I id, String name) {
        return repository.findById(id)
                .orElseThrow(() -> new IdNotFoundException(ErrorMessages.idNotFound(name)));
    }
}
