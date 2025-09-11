package com.chonect.backend.infrastructure.abstract_services.basic_abstract_services;

public interface DeleteService<ID> {
    public void delete(ID id);
}