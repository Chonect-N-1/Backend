package com.snapshot.chonect.infrastructure.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.models.ElementEntity;
import com.snapshot.chonect.domain.repositories.ElementRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IElementService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

@Service
public class ElementService extends BaseCrudService<ElementEntity, ElementRepository> implements IElementService {

    private final ElementRepository elementRepository;
    private final SupportService<ElementEntity> supportService;

    public ElementService(ElementRepository repository, SupportService<ElementEntity> supportService) {
        super(repository, supportService);
        this.elementRepository = repository;
        this.supportService = supportService;
    }

    @Override
    protected String getEntityName() {
        return "Element";
    }
}
