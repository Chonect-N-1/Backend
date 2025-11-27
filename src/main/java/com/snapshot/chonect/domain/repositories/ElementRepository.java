package com.snapshot.chonect.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snapshot.chonect.domain.models.ElementEntity;
import java.util.UUID;

@Repository
public interface ElementRepository extends JpaRepository<ElementEntity, UUID> {
    java.util.Optional<ElementEntity> findByElementId(String elementId);
}