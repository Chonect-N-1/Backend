package com.snapshot.chonect.domain.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "folder")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.PERSIST)
    private List<ProjectEntity> projects = new ArrayList<>();
}
