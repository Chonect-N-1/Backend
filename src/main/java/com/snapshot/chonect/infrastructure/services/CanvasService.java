package com.snapshot.chonect.infrastructure.services;

import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.models.CanvasEntity;
import com.snapshot.chonect.domain.repositories.CanvasRepository;
import com.snapshot.chonect.infrastructure.abstract_services.ICanvasService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;
import java.util.UUID;

@Service
public class CanvasService extends BaseCrudService<CanvasEntity, UUID, CanvasRepository> implements ICanvasService {

    public CanvasService(CanvasRepository repository, SupportService<CanvasEntity, UUID> supportService) {
        super(repository, supportService);
    }

    @Override
    protected String getEntityName() {
        return "Canvas";
    }
}
