package com.snapshot.chonect.infrastructure.services;

import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.models.ElementEntity;
import com.snapshot.chonect.domain.repositories.ElementRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IElementService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;
import java.util.UUID;

@Service
public class ElementService extends BaseCrudService<ElementEntity, UUID, ElementRepository> implements IElementService {

    public ElementService(ElementRepository repository, SupportService<ElementEntity, UUID> supportService) {
        super(repository, supportService);
    }

    @Override
    protected String getEntityName() {
        return "Element";
    }
}
