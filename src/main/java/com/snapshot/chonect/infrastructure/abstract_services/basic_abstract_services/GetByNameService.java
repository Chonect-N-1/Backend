package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface GetByNameService<RS, Name> {
    public RS getByName(Name name);
}