package com.snapshot.chonect.domain.models;

import com.snapshot.chonect.utils.objects.PageConfig;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.ToString;
import java.util.UUID;

@Entity(name = "page")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "project", "canvas" })
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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