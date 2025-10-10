package com.snapshot.chonect.infrastructure.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.ConnectionEntity;
import com.snapshot.chonect.domain.repositories.ConnectionRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IConnectionService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectionService implements IConnectionService {
    
    @Autowired
    private final ConnectionRepository connectionRepository;

    @Autowired
    private final SupportService<ConnectionEntity> supportService;

    @Override
    @Transactional
    public ConnectionEntity create(ConnectionEntity request) {
        try {
            return connectionRepository.save(request);
        } catch (Exception e) {
            throw new RuntimeException("Error creando connection: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ConnectionEntity getById(Long id) {
        return supportService.findById(connectionRepository, id, "Connection");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectionEntity> getAll() {
        return connectionRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (!connectionRepository.existsById(id)) {
                throw new RuntimeException("Connection no encontrado: " + id);
            }
            connectionRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando connection: " + e.getMessage(), e);
        }
    }
    
}
