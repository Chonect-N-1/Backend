package com.snapshot.chonect.infrastructure.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.CanvasEntity;
import com.snapshot.chonect.domain.repositories.CanvasRepository;
import com.snapshot.chonect.infrastructure.abstract_services.ICanvasService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CanvasService implements ICanvasService {

    @Autowired
    private final CanvasRepository canvasRepository;

    @Autowired
    private final SupportService<CanvasEntity> supportService;

    @Override
    @Transactional
    public CanvasEntity create(CanvasEntity request) {
        try {
            return canvasRepository.save(request);
        } catch (Exception e) {
            throw new RuntimeException("Error creando canvas: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CanvasEntity getById(Long id) {
        return supportService.findById(canvasRepository, id, "Canvas");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanvasEntity> getAll() {
        return canvasRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (!canvasRepository.existsById(id)) {
                throw new RuntimeException("Canvas no encontrado: " + id); // francamente en este caso no estoy controlando los errores
            }
            canvasRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando canvas: " + e.getMessage(), e);
        }
    }
    
}
