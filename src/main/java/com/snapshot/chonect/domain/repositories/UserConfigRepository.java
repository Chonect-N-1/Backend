package com.snapshot.chonect.domain.repositories;

import com.snapshot.chonect.domain.models.UserConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserConfigRepository extends JpaRepository<UserConfigEntity, UUID> {
}
