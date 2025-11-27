package com.snapshot.chonect.infrastructure.services;

import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.models.ConnectionEntity;
import com.snapshot.chonect.domain.repositories.ConnectionRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IConnectionService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;
import java.util.UUID;

@Service
public class ConnectionService extends BaseCrudService<ConnectionEntity, UUID, ConnectionRepository>
        implements IConnectionService {

    public ConnectionService(ConnectionRepository repository, SupportService<ConnectionEntity, UUID> supportService) {
        super(repository, supportService);
    }

    @Override
    protected String getEntityName() {
        return "Connection";
    }
}
