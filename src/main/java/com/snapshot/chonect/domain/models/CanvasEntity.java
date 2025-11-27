package com.snapshot.chonect.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "canvas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "elements", "connections" })
public class CanvasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "canvas_id")
    private List<ElementEntity> elements = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "canvas_id")
    private List<ConnectionEntity> connections = new ArrayList<>();
}
