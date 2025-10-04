package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface GetByEmailService<RS, Email> {
    public RS getByEmail(Email email);
}