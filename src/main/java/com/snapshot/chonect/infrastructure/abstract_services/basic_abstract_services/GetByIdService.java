package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface GetByIdService<T, I> {
    public T getById(I id);
}
