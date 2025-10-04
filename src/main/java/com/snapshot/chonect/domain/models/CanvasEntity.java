package com.snapshot.chonect.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "canvas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanvasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "canvas_id")
    private List<ElementEntity> elements = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "canvas_id")
    private List<ConnectionEntity> connections = new ArrayList<>();
}
