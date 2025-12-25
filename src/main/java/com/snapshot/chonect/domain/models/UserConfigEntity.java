package com.snapshot.chonect.domain.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(columnDefinition = "TEXT")
    private String themes; // Stores JSON string for frontend themes

    private String plan;

    private LocalDateTime planExpirationDate;
}
