package com.snapshot.chonect.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snapshot.chonect.domain.models.CanvasEntity;

@Repository
public interface CanvasRepository extends JpaRepository<CanvasEntity, Long> {
}
