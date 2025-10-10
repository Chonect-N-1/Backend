package com.snapshot.chonect.infrastructure.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.PageEntity;
import com.snapshot.chonect.domain.repositories.PageRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IPageService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PageService implements IPageService {

    @Autowired
    private final PageRepository pageRepository;

    @Autowired
    private final SupportService<PageEntity> supportService;

    @Override
    @Transactional
    public PageEntity create(PageEntity request) {
        try {
            return pageRepository.save(request);
        } catch (Exception e) {
            throw new RuntimeException("Error creando page: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageEntity getById(Long id) {
        return supportService.findById(pageRepository, id, "Page");
    }

    @Override
    @Transactional(readOnly = true)
    public List<PageEntity> getAll() {
        return pageRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (!pageRepository.existsById(id)) {
                throw new RuntimeException("Page no encontrado " + id);
            }
            pageRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando page: " + e.getMessage(), e);
        }
    }
    
}
