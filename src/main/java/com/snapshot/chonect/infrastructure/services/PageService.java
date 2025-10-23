package com.snapshot.chonect.infrastructure.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.models.PageEntity;
import com.snapshot.chonect.domain.repositories.PageRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IPageService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

@Service
public class PageService extends BaseCrudService<PageEntity, PageRepository> implements IPageService {

    private final PageRepository pageRepository;
    private final SupportService<PageEntity> supportService;

    public PageService(PageRepository repository, SupportService<PageEntity> supportService) {
        super(repository, supportService);
        this.pageRepository = repository;
        this.supportService = supportService;
    }

    @Override
    protected String getEntityName() {
        return "Page";
    }
}
