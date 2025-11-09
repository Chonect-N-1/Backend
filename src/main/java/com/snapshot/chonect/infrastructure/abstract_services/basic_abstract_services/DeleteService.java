package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

import org.springframework.lang.NonNull;

public interface DeleteService<T> {
    public void delete(@NonNull T id);
}
