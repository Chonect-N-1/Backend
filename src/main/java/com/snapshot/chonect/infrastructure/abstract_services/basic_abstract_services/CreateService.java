package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface CreateService<R, S> {
    public S create(R request);
}
