package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface GetByIdService<RS, ID> {
    public RS getById(ID id);
}
