package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface GetByNameOrEmailService<RS, Search> {
    public RS getByNameOrEmail(Search search);
}