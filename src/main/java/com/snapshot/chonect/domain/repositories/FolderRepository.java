package com.snapshot.chonect.domain.repositories;

import com.snapshot.chonect.domain.models.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FolderRepository extends JpaRepository<FolderEntity, UUID> {
}
