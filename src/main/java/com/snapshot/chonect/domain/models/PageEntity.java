package com.snapshot.chonect.domain.models;

import com.snapshot.chonect.utils.objects.PageConfig;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Entity(name = "page")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String pageName;
    
    @Embedded
    private PageConfig config;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "canvas_id")
    private CanvasEntity canvas;
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
}