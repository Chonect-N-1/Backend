package com.snapshot.chonect.api.graphql;

import lombok.Data;

@Data
public class PageInput {
    private String pageName;
    private PageConfigInput config;
    private CanvasInput canvas;
}
