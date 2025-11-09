package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

import org.springframework.lang.NonNull;

public interface CreateService<R, S> {
    public S create(@NonNull R request);
}
