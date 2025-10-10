package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

import org.springframework.data.domain.Page;

public interface GetAllPageableService<RS> {
    public Page<RS> getAllPageable(int page, int size);
}