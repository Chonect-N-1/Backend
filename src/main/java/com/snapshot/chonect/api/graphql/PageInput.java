package com.snapshot.chonect.api.graphql;

import lombok.Data;

@Data
public class PageInput {
    private String id; // UUID de la página existente (null si es nueva)
    private String pageName;
    private PageConfigInput config;
    private CanvasInput canvas;
}
