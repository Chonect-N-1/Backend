package com.snapshot.chonect.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snapshot.chonect.domain.models.PageEntity;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Long> {

}
