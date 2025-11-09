package com.snapshot.chonect.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import java.io.Serializable;
import java.util.UUID;

@Entity(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @NonNull
    @Column(length = 3, nullable = false, unique = true)
    private String code; // ISO 3166-1 alpha-3 code (e.g., "COL", "USA", "ESP")
}
