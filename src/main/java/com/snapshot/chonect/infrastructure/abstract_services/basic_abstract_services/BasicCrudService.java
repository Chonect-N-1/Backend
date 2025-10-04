package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface BasicCrudService<RCQ, RUQ, RS, BRS, ID> extends
        CreateService<RCQ, BRS>,
        GetByIdService<RS, ID>,
        UpdateService<RS, RUQ, ID>,
        DeleteService<ID> {
}