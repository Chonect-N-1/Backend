package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

import org.springframework.lang.NonNull;

public interface GetByIdService<T, I> {
    public T getById(@NonNull I id);
}
