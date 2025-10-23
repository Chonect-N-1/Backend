package com.snapshot.chonect.infrastructure.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.models.ConnectionEntity;
import com.snapshot.chonect.domain.repositories.ConnectionRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IConnectionService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

@Service
public class ConnectionService extends BaseCrudService<ConnectionEntity, ConnectionRepository> implements IConnectionService {

    private final ConnectionRepository connectionRepository;
    private final SupportService<ConnectionEntity> supportService;

    public ConnectionService(ConnectionRepository repository, SupportService<ConnectionEntity> supportService) {
        super(repository, supportService);
        this.connectionRepository = repository;
        this.supportService = supportService;
    }

    @Override
    protected String getEntityName() {
        return "Connection";
    }
}
