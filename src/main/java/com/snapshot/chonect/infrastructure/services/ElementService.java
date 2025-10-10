package com.snapshot.chonect.infrastructure.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.ElementEntity;
import com.snapshot.chonect.domain.repositories.ElementRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IElementService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ElementService implements IElementService {

    @Autowired
    private final ElementRepository elementRepository;

    @Autowired
    private final SupportService<ElementEntity> supportService;

    @Override
    @Transactional
    public ElementEntity create(ElementEntity request) {
        try {
            return elementRepository.save(request);
        } catch (Exception e) {
            throw new RuntimeException("Error Creando element: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ElementEntity getById(Long id) {
        return supportService.findById(elementRepository, id, "Element");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> getAll() {
        return elementRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (!elementRepository.existsById(id)) {
                throw new RuntimeException("Element no encontrado: " + id);
            }
            elementRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando elemento: " + e.getMessage(), e);
        }
    }
    
}
