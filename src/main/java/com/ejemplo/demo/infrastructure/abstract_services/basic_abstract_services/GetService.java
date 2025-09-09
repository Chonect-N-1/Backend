package com.ejemplo.demo.infrastructure.abstract_services.basic_abstract_services;

public interface GetService<RS, ID> {
    public RS get(ID id);
}
