package com.snapshot.chonect.domain.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snapshot.chonect.domain.models.LanguageEntity;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageEntity, UUID> {
}
