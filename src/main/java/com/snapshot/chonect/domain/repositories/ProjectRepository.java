package com.snapshot.chonect.domain.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snapshot.chonect.domain.models.ProjectEntity;
import com.snapshot.chonect.domain.models.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {
    boolean existsByProjectNameAndUser(String projectName, UserEntity user);

    @EntityGraph(attributePaths = { "pages", "pages.canvas", "pages.canvas.elements", "pages.canvas.connections" })
    List<ProjectEntity> findAllBy();

    @EntityGraph(attributePaths = { "pages", "pages.canvas", "pages.canvas.elements", "pages.canvas.connections",
            "user" })
    Optional<ProjectEntity> findWithPagesById(UUID id);
}