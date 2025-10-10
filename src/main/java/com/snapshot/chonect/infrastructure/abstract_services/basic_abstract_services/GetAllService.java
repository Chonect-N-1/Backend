package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

import java.util.List;

public interface GetAllService<RS> {
    public List<RS> getAll();
}