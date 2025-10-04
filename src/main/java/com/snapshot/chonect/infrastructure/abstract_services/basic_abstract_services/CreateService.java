package com.snapshot.chonect.infrastructure.abstract_services.basic_abstract_services;

public interface CreateService<RCQ, BRS> {
    public BRS create(RCQ request);
}