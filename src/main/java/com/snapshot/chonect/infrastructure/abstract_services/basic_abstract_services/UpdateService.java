package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface UpdateService<T, U, I> {
    public T update(U request, I id);
}
